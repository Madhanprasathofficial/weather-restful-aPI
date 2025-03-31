package com.weather.api.weatherapi.controller;

import com.weather.api.weatherapi.service.ApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ApiKeyServiceTest {

    private ApiKeyService apiKeyService;

    @BeforeEach
    void setUp() {
        // Initialize the service with predefined valid API keys
        apiKeyService = new ApiKeyService();
    }

    /**
     * Test validating an existing API key.
     */
    @Test
    void testIsValidKey_ValidKey() {
        // Arrange
        String validApiKey = "e7dd890a480d1e9547cd9d92b2f803c7";

        // Act
        boolean isValid = apiKeyService.isValidKey(validApiKey);

        // Assert
        assertTrue(isValid, "The API key should be valid.");
    }

    /**
     * Test validating a non-existent API key.
     */
    @Test
    void testIsValidKey_InvalidKey() {
        // Arrange
        String invalidApiKey = "INVALID_API_KEY";

        // Act
        boolean isValid = apiKeyService.isValidKey(invalidApiKey);

        // Assert
        assertFalse(isValid, "The API key should be invalid.");
    }

    /**
     * Test adding a new API key.
     */
    @Test
    void testAddApiKey() {
        // Arrange
        String newApiKey = "NEW_API_KEY_1234567890abcdef";

        // Act
        apiKeyService.addApiKey(newApiKey);

        // Assert
        assertTrue(apiKeyService.isValidKey(newApiKey), "The new API key should be valid after adding.");
    }

    /**
     * Test deleting an existing API key.
     */
    @Test
    void testDeleteApiKey() {
        // Arrange
        String existingApiKey = "e7dd890a480d1e9547cd9d92b2f803c7";

        // Act
        apiKeyService.deleteApiKey(existingApiKey);

        // Assert
        assertFalse(apiKeyService.isValidKey(existingApiKey), "The API key should be invalid after deletion.");
    }

    /**
     * Test listing all API keys.
     */
    @Test
    void testListApiKeys() {
        // Arrange
        Set<String> expectedApiKeys = Set.of(
                "147854e652b5b992ec688497963df829",
                "b2180c8ac8633b32549bb10ac4ca7730",
                "bc6faa4243d1bf3acef6c4f5cd862c1f",
                "e7dd890a480d1e9547cd9d92b2f803c7",
                "5ceca6dbfe14418a07e12fc76ec7d1bb"
        );

        // Act
        Set<String> actualApiKeys = apiKeyService.listApiKeys();

        // Assert
        assertEquals(expectedApiKeys, actualApiKeys, "The listed API keys should match the predefined set.");
    }
}