package com.weather.api.weatherapi.dto;

/**
 * Data Transfer Object (DTO) representing the response for weather data.
 * <p>
 * This record contains the weather description returned by the API.
 *
 * @param description The weather description (e.g., "clear sky", "rain", etc.)
 */
public record WeatherResponse(String description) {}