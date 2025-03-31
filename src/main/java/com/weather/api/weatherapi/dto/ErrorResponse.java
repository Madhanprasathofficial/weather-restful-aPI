package com.weather.api.weatherapi.dto;

/**
 * Data Transfer Object (DTO) representing an error response.
 * <p>
 * This record is used to encapsulate error messages returned by the API in case of failures.
 *
 * @param error The error message describing the issue (e.g., "Invalid API key").
 */
public record ErrorResponse(String error) {}