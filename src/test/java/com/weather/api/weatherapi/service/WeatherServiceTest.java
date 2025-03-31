package com.weather.api.weatherapi.service;

import com.weather.api.weatherapi.dto.WeatherApiResponse;
import com.weather.api.weatherapi.dto.WeatherData;
import com.weather.api.weatherapi.exception.InvalidApiKeyException;
import com.weather.api.weatherapi.exception.WeatherServiceException;
import com.weather.api.weatherapi.repository.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherServiceTest {

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RateLimitService rateLimitService;

    @Mock
    private ApiKeyService apiKeyService; // Add mock for ApiKeyService

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test fetching cached weather data with a valid API key.
     */
    @Test
    void testGetWeather_CachedData_ValidApiKey() {
        // Arrange
        String city = "London";
        String country = "UK";
        String validApiKey = "VALID_API_KEY";

        // Mock API key validation to pass
        when(apiKeyService.isValidKey(validApiKey)).thenReturn(true);

        // Mock rate limit validation
        doNothing().when(rateLimitService).validateRateLimit(validApiKey);

        // Mock cached data
        WeatherData cachedData = new WeatherData(city, country, "clear sky");
        when(weatherRepository.findByCityAndCountry(city, country)).thenReturn(Optional.of(cachedData));

        // Act
        WeatherData result = weatherService.getWeather(city, country, validApiKey);

        // Assert
        assertNotNull(result);
        assertEquals("clear sky", result.getDescription());

        // Verify interactions
        verify(apiKeyService, times(1)).isValidKey(validApiKey);
        verify(rateLimitService, times(1)).validateRateLimit(validApiKey);
        verify(weatherRepository, times(1)).findByCityAndCountry(city, country);
        verify(restTemplate, never()).getForObject(anyString(), eq(WeatherApiResponse.class));
    }

    /**
     * Test handling invalid API key.
     */
    @Test
    void testGetWeather_InvalidApiKey() {
        // Arrange
        String city = "London";
        String country = "UK";
        String invalidApiKey = "INVALID_API_KEY";

        // Mock API key validation to fail
        when(apiKeyService.isValidKey(invalidApiKey)).thenReturn(false);

        // Act & Assert
        InvalidApiKeyException exception = assertThrows(InvalidApiKeyException.class, () -> {
            weatherService.getWeather(city, country, invalidApiKey);
        });
        assertEquals("Invalid API key", exception.getMessage());

        // Verify that subsequent methods are not called
        verify(apiKeyService, times(1)).isValidKey(invalidApiKey);
        verify(rateLimitService, never()).validateRateLimit(anyString());
        verify(weatherRepository, never()).findByCityAndCountry(anyString(), anyString());
        verify(restTemplate, never()).getForObject(anyString(), eq(WeatherApiResponse.class));
    }
    /**
     * Test fetching new weather data from OpenWeatherMap with a valid API key.
     */
    @Test
    void testGetWeather_NewData_ValidApiKey() {
        // Arrange
        String city = "Tokyo";
        String country = "JP";
        String validApiKey = "VALID_API_KEY";

        // Mock API key validation to pass
        when(apiKeyService.isValidKey(validApiKey)).thenReturn(true);

        // Mock rate limit validation
        doNothing().when(rateLimitService).validateRateLimit(validApiKey);

        // Mock no cached data
        when(weatherRepository.findByCityAndCountry(city, country)).thenReturn(Optional.empty());

        // Mock OpenWeatherMap API response
        WeatherApiResponse apiResponse = new WeatherApiResponse();
        WeatherApiResponse.Weather weatherItem = new WeatherApiResponse.Weather();
        weatherItem.setDescription("clear sky");
        apiResponse.setWeather(Collections.singletonList(weatherItem));
        when(restTemplate.getForObject(anyString(), eq(WeatherApiResponse.class))).thenReturn(apiResponse);

        // Mock saving to the database
        WeatherData savedData = new WeatherData(city, country, "clear sky");
        when(weatherRepository.save(any(WeatherData.class))).thenReturn(savedData);

        // Act
        WeatherData result = weatherService.getWeather(city, country, validApiKey);

        // Assert
        assertNotNull(result);
        assertEquals("clear sky", result.getDescription());

        // Verify interactions
        verify(apiKeyService, times(1)).isValidKey(validApiKey);
        verify(rateLimitService, times(1)).validateRateLimit(validApiKey);
        verify(weatherRepository, times(1)).findByCityAndCountry(city, country);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(WeatherApiResponse.class));
        verify(weatherRepository, times(1)).save(any(WeatherData.class));
    }

    /**
     * Test exception handling for invalid OpenWeatherMap response.
     */
    @Test
    void testFetchAndSaveWeatherData_Exception() {
        // Arrange
        String city = "InvalidCity";
        String country = "XX";
        String validApiKey = "VALID_API_KEY";

        // Mock API key validation to pass
        when(apiKeyService.isValidKey(validApiKey)).thenReturn(true);

        // Mock rate limit validation
        doNothing().when(rateLimitService).validateRateLimit(validApiKey);

        // Mock no cached data
        when(weatherRepository.findByCityAndCountry(city, country)).thenReturn(Optional.empty());

        // Mock OpenWeatherMap API response as null
        when(restTemplate.getForObject(anyString(), eq(WeatherApiResponse.class))).thenReturn(null);

        // Act & Assert
        WeatherServiceException exception = assertThrows(WeatherServiceException.class, () -> {
            weatherService.getWeather(city, country, validApiKey);
        });
        assertEquals("Failed to fetch weather data: No weather data found for city: InvalidCity, country: XX", exception.getMessage());

        // Verify interactions
        verify(apiKeyService, times(1)).isValidKey(validApiKey);
        verify(rateLimitService, times(1)).validateRateLimit(validApiKey);
        verify(weatherRepository, times(1)).findByCityAndCountry(city, country);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(WeatherApiResponse.class));
    }

    /**
     * Test database health check success.
     */
    @Test
    void testIsDatabaseHealthy_Success() {
        // Arrange
        when(weatherRepository.healthCheck()).thenReturn(1);

        // Act
        boolean isHealthy = weatherService.isDatabaseHealthy();

        // Assert
        assertTrue(isHealthy);
        verify(weatherRepository, times(1)).healthCheck();
    }

    /**
     * Test database health check failure.
     */
    @Test
    void testIsDatabaseHealthy_Failure() {
        // Arrange
        when(weatherRepository.healthCheck()).thenThrow(new RuntimeException("Database connection failed"));

        // Act
        boolean isHealthy = weatherService.isDatabaseHealthy();

        // Assert
        assertFalse(isHealthy);
        verify(weatherRepository, times(1)).healthCheck();
    }
}