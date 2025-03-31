package com.weather.api.weatherapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Service to manage API keys.
 * <p>
 * This service provides functionality to:
 * - Validate API keys.
 * - Add new API keys.
 * - Delete existing API keys.
 * - List all valid API keys.
 */
@Slf4j
@Service
public class ApiKeyService {

    /**
     * A thread-safe set to store valid API keys.
     */
    private final Set<String> validApiKeys = new HashSet<>();

    /**
     * Initializes the service with predefined valid API keys.
     */
    public ApiKeyService() {
        // Predefined API keys (replace these with your actual keys)
        validApiKeys.add("b2180c8ac8633b32549bb10ac4ca7730");
        validApiKeys.add("e7dd890a480d1e9547cd9d92b2f803c7");
        validApiKeys.add("5ceca6dbfe14418a07e12fc76ec7d1bb");
        validApiKeys.add("147854e652b5b992ec688497963df829");
        validApiKeys.add("bc6faa4243d1bf3acef6c4f5cd862c1f");
        log.info("ApiKeyService initialized with {} valid API keys.", validApiKeys.size());
    }

    /**
     * Validates if an API key is valid.
     *
     * @param apiKey The API key to validate
     * @return True if the key is valid, false otherwise
     */
    public boolean isValidKey(String apiKey) {
        boolean isValid = validApiKeys.contains(apiKey);
        if (!isValid) {
            log.warn("Invalid API key used: {}", apiKey);
        } else {
            log.info("Valid API key used: {}", apiKey);
        }
        return isValid;
    }

    /**
     * Adds a new API key.
     *
     * @param apiKey The API key to add
     */
    public void addApiKey(String apiKey) {
        validApiKeys.add(apiKey);
        log.info("Added new API key: {}", apiKey);
    }

    /**
     * Deletes an existing API key.
     *
     * @param apiKey The API key to delete
     */
    public void deleteApiKey(String apiKey) {
        boolean removed = validApiKeys.remove(apiKey);
        if (removed) {
            log.info("Deleted API key: {}", apiKey);
        } else {
            log.warn("Attempted to delete non-existent API key: {}", apiKey);
        }
    }

    /**
     * Lists all valid API keys.
     *
     * @return A set of valid API keys (a copy to prevent external modification)
     */
    public Set<String> listApiKeys() {
        return new HashSet<>(validApiKeys); // Return a copy to prevent modification
    }
}