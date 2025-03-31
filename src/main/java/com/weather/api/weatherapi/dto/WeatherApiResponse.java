package com.weather.api.weatherapi.dto;

import lombok.Data;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing the response from the OpenWeatherMap API.
 * <p>
 * This class maps the structure of the JSON response returned by the OpenWeatherMap API.
 */
@Data
public class WeatherApiResponse {

    /**
     * The list of weather conditions for a specific location.
     * <p>
     * Each element in the list represents a weather condition, such as "clear sky" or "rain".
     */
    private List<Weather> weather; // Maps the "weather" array in the JSON

    /**
     * Inner class representing a single weather condition.
     */
    @Data
    public static class Weather {

        /**
         * The description of the weather condition (e.g., "clear sky", "rain").
         */
        private String description; // Maps "weather[].description"
    }
}