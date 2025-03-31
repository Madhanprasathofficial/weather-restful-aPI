package com.weather.api.weatherapi.dto;

/**
 * Data Transfer Object (DTO) representing the response for a health check.
 * <p>
 * This record contains the status of the service and the health of the database.
 *
 * @param status          The overall status of the service (e.g., "OK").
 * @param databaseHealthy Indicates whether the database is healthy (true/false).
 */
public record HealthResponse(String status, boolean databaseHealthy) {}