package com.weather.api.weatherapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ApiKeyServiceTest {

    private ApiKeyService apiKeyService;

    @BeforeEach
    void setUp() {
        apiKeyService = new ApiKeyService(); // Use the actual service, not a mock
    }

    /**
     * Test validating a valid API key.
     */
    @Test
    void testIsValidKey_ValidKey() {
        String validApiKey = "e7dd890a480d1e9547cd9d92b2f803c7";
        assertTrue(apiKeyService.isValidKey(validApiKey));
    }

    /**
     * Test validating an invalid API key.
     */
    @Test
    void testIsValidKey_InvalidKey() {
        String invalidApiKey = "INVALID_API_KEY";
        assertFalse(apiKeyService.isValidKey(invalidApiKey));
    }

    /**
     * Test adding a new API key.
     */
    @Test
    void testAddApiKey() {
        String newApiKey = "NEW_API_KEY_1234567890abcdef";
        apiKeyService.addApiKey(newApiKey);
        assertTrue(apiKeyService.isValidKey(newApiKey));
    }

    /**
     * Test deleting an existing API key.
     */
    @Test
    void testDeleteApiKey() {
        String existingApiKey = "e7dd890a480d1e9547cd9d92b2f803c7";
        apiKeyService.deleteApiKey(existingApiKey);
        assertFalse(apiKeyService.isValidKey(existingApiKey));
    }

    /**
     * Test listing all valid API keys.
     */
    @Test
    void testListApiKeys() {
        Set<String> expectedKeys = Set.of(
                "b2180c8ac8633b32549bb10ac4ca7730",
                "e7dd890a480d1e9547cd9d92b2f803c7",
                "5ceca6dbfe14418a07e12fc76ec7d1bb",
                "147854e652b5b992ec688497963df829",
                "bc6faa4243d1bf3acef6c4f5cd862c1f"
        );
        Set<String> actualKeys = apiKeyService.listApiKeys();
        assertEquals(expectedKeys, actualKeys);
    }
}