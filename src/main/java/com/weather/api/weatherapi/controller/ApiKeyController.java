package com.weather.api.weatherapi.controller;

import com.weather.api.weatherapi.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/key")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "API Key Management", description = "Endpoints for managing and validating API keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    /**
     * Add a new API key.
     *
     * @param apiKey The API key to add
     * @return ResponseEntity with success message or error
     */
    @PostMapping("/add")
    @Operation(
            summary = "Add a new API key",
            description = "Adds a new API key to the system if it does not already exist."
    )
    @ApiResponse(responseCode = "201", description = "API key added successfully")
    @ApiResponse(responseCode = "400", description = "API key is null or empty")
    @ApiResponse(responseCode = "409", description = "API key already exists")
    public ResponseEntity<String> addApiKey(
            @RequestParam
            @Parameter(description = "The API key to add", required = true) String apiKey) {

        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("Attempted to add an invalid API key: {}", apiKey);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("API key cannot be null or empty.");
        }

        if (apiKeyService.isValidKey(apiKey)) {
            log.warn("Attempted to add an already existing API key: {}", apiKey);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("API key already exists.");
        }

        apiKeyService.addApiKey(apiKey);
        log.info("Added new API key: {}", apiKey);
        return ResponseEntity.status(HttpStatus.CREATED).body("API key added successfully.");
    }

    /**
     * Delete an existing API key.
     *
     * @param apiKey The API key to delete
     * @return ResponseEntity with success message or error
     */
    @DeleteMapping("/delete")
    @Operation(
            summary = "Delete an existing API key",
            description = "Deletes an existing API key from the system."
    )
    @ApiResponse(responseCode = "200", description = "API key deleted successfully")
    @ApiResponse(responseCode = "400", description = "API key is null or empty")
    @ApiResponse(responseCode = "404", description = "API key does not exist")
    public ResponseEntity<String> deleteApiKey(
            @RequestParam
            @Parameter(description = "The API key to delete", required = true) String apiKey) {

        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("Attempted to delete an invalid API key: {}", apiKey);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("API key cannot be null or empty.");
        }

        if (!apiKeyService.isValidKey(apiKey)) {
            log.warn("Attempted to delete a non-existent API key: {}", apiKey);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("API key does not exist.");
        }

        apiKeyService.deleteApiKey(apiKey);
        log.info("Deleted API key: {}", apiKey);
        return ResponseEntity.ok("API key deleted successfully.");
    }

    /**
     * List all valid API keys.
     *
     * @return ResponseEntity with a set of valid API keys
     */
    @GetMapping("/list")
    @Operation(
            summary = "List all valid API keys",
            description = "Retrieves a list of all valid API keys in the system."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved API keys")
    public ResponseEntity<Set<String>> listApiKeys() {
        Set<String> apiKeys = apiKeyService.listApiKeys();
        log.info("Listing all API keys: {}", apiKeys);
        return ResponseEntity.ok(apiKeys);
    }

    /**
     * Validate an API key.
     *
     * @param apiKey The API key to validate
     * @return ResponseEntity with validation result
     */
    @PostMapping("/validate")
    @Operation(
            summary = "Validate an API key",
            description = "Validates whether the provided API key is valid."
    )
    @ApiResponse(responseCode = "200", description = "Validation result returned")
    public ResponseEntity<Map<String, Boolean>> validateApiKey(
            @RequestParam
            @Parameter(description = "The API key to validate", required = true) String apiKey) {

        boolean isValid = apiKeyService.isValidKey(apiKey);
        log.info("Validating API key: {}. Result: {}", apiKey, isValid);

        // Return the validation result as JSON
        Map<String, Boolean> response = Map.of("isValid", isValid);
        return ResponseEntity.ok(response);
    }
}