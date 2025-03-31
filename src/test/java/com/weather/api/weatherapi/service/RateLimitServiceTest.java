package com.weather.api.weatherapi.service;

import com.weather.api.weatherapi.exception.RateLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitServiceTest {

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rateLimitService = new RateLimitService(); // Use the actual service, not a mock
    }

    /**
     * Test rate limit validation for a valid API key within the allowed limit.
     */
    @Test
    void testValidateRateLimit_ValidRequests() {
        String apiKey = "API_KEY_1";

        // Make 5 valid requests
        for (int i = 0; i < 5; i++) {
            assertDoesNotThrow(() -> rateLimitService.validateRateLimit(apiKey));
        }
    }

    /**
     * Test rate limit validation for an API key exceeding the allowed limit.
     */
    @Test
    void testValidateRateLimit_ExceedLimit() {
        String apiKey = "API_KEY_1";

        // Make 5 valid requests
        for (int i = 0; i < 5; i++) {
            assertDoesNotThrow(() -> rateLimitService.validateRateLimit(apiKey));
        }

        // Attempt a 6th request (should exceed the limit)
        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, () -> {
            rateLimitService.validateRateLimit(apiKey);
        });
        assertEquals("Rate limit exceeded", exception.getMessage());
    }

    /**
     * Test resetting the request counts after an hour.
     */
    @Test
    void testResetCounts() throws InterruptedException {
        String apiKey = "API_KEY_1";

        // Make 5 valid requests
        for (int i = 0; i < 5; i++) {
            assertDoesNotThrow(() -> rateLimitService.validateRateLimit(apiKey));
        }

        // Manually invoke the reset method (simulating the scheduled task)
        rateLimitService.resetCounts();

        // Make another 5 valid requests after reset
        for (int i = 0; i < 5; i++) {
            assertDoesNotThrow(() -> rateLimitService.validateRateLimit(apiKey));
        }
    }
}