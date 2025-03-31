package com.weather.api.weatherapi.controller;

import com.weather.api.weatherapi.dto.*;
import com.weather.api.weatherapi.exception.*;
import com.weather.api.weatherapi.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test getting weather data successfully.
     */
    @Test
    void testGetWeather_Success() {
        // Arrange
        String city = "Tokyo";
        String country = "JP";
        String apiKey = "API_KEY_1_e7dd890a480d1e9547cd9d92b2f803c7";
        WeatherData mockWeatherData = new WeatherData(city, country, "clear sky");
        when(weatherService.getWeather(city, country, apiKey)).thenReturn(mockWeatherData);

        // Act
        ResponseEntity<WeatherResponse> response = weatherController.getWeather(city, country, apiKey);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("clear sky", response.getBody().description()); // Use the record's accessor method
        verify(weatherService, times(1)).getWeather(city, country, apiKey);
    }

    /**
     * Test health check endpoint.
     */
    @Test
    void testHealthCheck() {
        // Arrange
        boolean dbHealthy = true;
        when(weatherService.isDatabaseHealthy()).thenReturn(dbHealthy);

        // Act
        ResponseEntity<HealthResponse> response = weatherController.healthCheck();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().databaseHealthy());
        verify(weatherService, times(1)).isDatabaseHealthy();
    }

    /**
     * Test handling invalid API key exception.
     */
    @Test
    void testHandleInvalidApiKey() {
        // Act
        ResponseEntity<ErrorResponse> response = weatherController.handleInvalidApiKey();

        // Assert
        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Invalid API key", response.getBody().error());
    }

    /**
     * Test handling rate limit exceeded exception.
     */
    @Test
    void testHandleRateLimitExceeded() {
        // Act
        ResponseEntity<ErrorResponse> response = weatherController.handleRateLimitExceeded();

        // Assert
        assertEquals(429, response.getStatusCodeValue());
        assertEquals("Rate limit exceeded", response.getBody().error());
    }

    /**
     * Test handling external service unavailable exception.
     */
    @Test
    void testHandleServiceError() {
        // Act
        ResponseEntity<ErrorResponse> response = weatherController.handleServiceError();

        // Assert
        assertEquals(503, response.getStatusCodeValue());
        assertEquals("External service unavailable", response.getBody().error());
    }
}