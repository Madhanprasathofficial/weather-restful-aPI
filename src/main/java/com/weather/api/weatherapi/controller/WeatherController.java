package com.weather.api.weatherapi.controller;

import com.weather.api.weatherapi.dto.*;
import com.weather.api.weatherapi.exception.*;
import com.weather.api.weatherapi.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Weather API controller providing endpoints for weather data and health checks.
 *
 * This controller handles:
 *
 *  Retrieving weather descriptions from OpenWeatherMap (with caching)
 *  Health checks for service and database connectivity
 *  API key validation and rate limiting
 *
 *
 * @author Madhanprasath
 */
@Slf4j
@Tag(name = "Weather API", description = "Endpoints for weather data and health checks")
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Validated
public class WeatherController {

    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);
    private final WeatherService weatherService;


    /**
     * Retrieves the weather description for a specified city and country.
     *
     * This method first checks if the weather data is cached in the H2 database. If not,
     * it fetches data from OpenWeatherMap, caches it, and returns the description.
     *
     * @param city    the city name (e.g., "London") (required)
     * @param country the country code (e.g., "UK") (required)
     * @param apiKey  the API key for authentication (one of API_KEY_1 to API_KEY_5) (required)
     * @return ResponseEntity containing the weather description
     * @throws InvalidApiKeyException      if the API key is invalid
     * @throws RateLimitExceededException  if the API key's rate limit is exceeded
     * @throws WeatherServiceException     if the external service is unavailable
     */

    @Operation(
            summary = "Get weather description",
            description = "Fetches weather description for a city/country using an API key. " +
                    "Cached data is returned if available."
    )
    @ApiResponse(responseCode = "200", description = "Successful response")
    @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    @ApiResponse(responseCode = "403", description = "Invalid API key")
    @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    @GetMapping("/getWeather")
    public ResponseEntity<WeatherResponse> getWeather(
            @Parameter(description = "City name (e.g., 'London')", required = true)
            @RequestParam @NotBlank(message = "City is required") String city,

            @Parameter(description = "Country code (e.g., 'UK')", required = true)
            @RequestParam @NotBlank(message = "Country is required") String country,

            @Parameter(description = "API key (one of API_KEY_1 to API_KEY_5)", required = true)
            @RequestParam @NotBlank(message = "API key is required") String apiKey) {

        logger.debug("API Key in Controller: " + apiKey);
        logger.debug("City: " + city);
        logger.debug("Country: " + country);
        WeatherData data = weatherService.getWeather(city, country, apiKey);
        return ResponseEntity.ok(new WeatherResponse(data.getDescription()));
    }


    /**
     * Checks the health of the service and database connectivity.
     *
     * @return ResponseEntity indicating service status and database health
     */
    @Operation(
            summary = "Health check",
            description = "Verifies service availability and database connectivity"
    )
    @ApiResponse(responseCode = "200", description = "Service healthy")
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> healthCheck() {
        boolean dbHealthy = weatherService.isDatabaseHealthy();
        return ResponseEntity.ok(new HealthResponse("OK", dbHealthy));
    }

    /**
     * Handles invalid API key errors.
     *
     * @return error response with 403 status
     */
    @ExceptionHandler(InvalidApiKeyException.class)
    @ApiResponse(responseCode = "403", description = "Invalid API key")
    public ResponseEntity<ErrorResponse> handleInvalidApiKey() {
        return ResponseEntity.status(403)
                .body(new ErrorResponse("Invalid API key"));
    }

    /**
     * Handles rate limit exceeded errors.
     *
     * @return error response with 429 status
     */
    @ExceptionHandler(RateLimitExceededException.class)
    @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded() {
        return ResponseEntity.status(429)
                .body(new ErrorResponse("Rate limit exceeded"));
    }

    /**
     * Handles external service unavailability errors.
     *
     * @return error response with 503 status
     */
    @ExceptionHandler(WeatherServiceException.class)
    @ApiResponse(responseCode = "503", description = "External service unavailable")
    public ResponseEntity<ErrorResponse> handleServiceError() {
        return ResponseEntity.status(503)
                .body(new ErrorResponse("External service unavailable"));
    }
}