package com.weather.api.weatherapi.service;

import com.weather.api.weatherapi.dto.WeatherApiResponse;
import com.weather.api.weatherapi.dto.WeatherData;
import com.weather.api.weatherapi.exception.InvalidApiKeyException;
import com.weather.api.weatherapi.exception.WeatherServiceException;
import com.weather.api.weatherapi.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherRepository weatherRepository;
    private final RestTemplate restTemplate;
    private final RateLimitService rateLimitService;
    private final ApiKeyService apiKeyService;
    private static final String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5/weather";

    /**
     * Retrieves weather data for a city/country, using caching and rate limiting.
     *
     * @param city    The city name (e.g., "London")
     * @param country The country code (e.g., "UK")
     * @param apiKey  The API key for authentication
     * @return WeatherData containing the weather description
     * @throws InvalidApiKeyException   If the API key is invalid
     * @throws WeatherServiceException  If the external service fails or rate limit is exceeded
     */
    public WeatherData getWeather(String city, String country, String apiKey) {
        log.debug("Processing weather request for city: {}, country: {}, apiKey: {}", city, country, apiKey);

        // Validate API key
        if (!apiKeyService.isValidKey(apiKey)) {
            log.warn("Invalid API key used: {}", apiKey);
            throw new InvalidApiKeyException();
        }

        // Validate rate limit
        rateLimitService.validateRateLimit(apiKey);
        log.info("Rate limit validated successfully for API Key: {}", apiKey);

        // Retrieve cached or fetch new weather data
        return getOrCreateWeatherData(city, country, apiKey);
    }

    /**
     * Retrieves cached weather data or fetches new data from OpenWeatherMap.
     *
     * @param city    The city name
     * @param country The country code
     * @param apiKey  The API key for authentication
     * @return WeatherData (cached or newly fetched)
     */
    private WeatherData getOrCreateWeatherData(String city, String country, String apiKey) {
        Optional<WeatherData> cachedData = weatherRepository.findByCityAndCountry(city, country);
        if (cachedData.isPresent()) {
            log.info("Returning cached weather data for city: {}, country: {}", city, country);
            return cachedData.get();
        }
        log.info("No cached data found. Fetching new weather data for city: {}, country: {}", city, country);
        return fetchAndSaveWeatherData(city, country, apiKey);
    }

    /**
     * Fetches weather data from OpenWeatherMap and saves it to the database.
     *
     * @param city    The city name
     * @param country The country code
     * @param apiKey  The API key for authentication
     * @return Saved WeatherData
     * @throws WeatherServiceException If the external API call fails
     */
    private WeatherData fetchAndSaveWeatherData(String city, String country, String apiKey) {
        String url = UriComponentsBuilder.fromUriString(OPEN_WEATHER_MAP_URL)
                .queryParam("q", city + "," + country)
                .queryParam("appid", apiKey)
                .toUriString();

        try {
            log.info("Fetching weather data from OpenWeatherMap for city: {}, country: {}", city, country);
            WeatherApiResponse response = restTemplate.getForObject(url, WeatherApiResponse.class);

            if (response == null || response.getWeather() == null || response.getWeather().isEmpty()) {
                throw new WeatherServiceException("No weather data found for city: " + city + ", country: " + country);
            }

            String description = response.getWeather().get(0).getDescription();
            log.info("Weather data fetched successfully: {}", description);

            // Save to database
            WeatherData weatherData = new WeatherData(city, country, description);
            return weatherRepository.save(weatherData);
        } catch (HttpClientErrorException e) {
            log.error("HTTP error while fetching weather data: Status Code: {}, Message: {}", e.getStatusCode(), e.getMessage());
            throw new WeatherServiceException("Failed to fetch weather data: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while fetching weather data: {}", e.getMessage());
            throw new WeatherServiceException("Failed to fetch weather data: " + e.getMessage());
        }
    }

    /**
     * Checks database connectivity by executing a simple query.
     *
     * @return True if the database is reachable, false otherwise
     */
    public boolean isDatabaseHealthy() {
        try {
            log.info("Performing database health check...");
            weatherRepository.healthCheck();
            log.info("Database health check passed.");
            return true;
        } catch (Exception e) {
            log.error("Database health check failed: {}", e.getMessage());
            return false;
        }
    }
}