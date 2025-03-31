package com.weather.api.weatherapi.dto;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity class representing weather data stored in the database.
 * <p>
 * This class maps to the `weather_data` table and contains information about
 * the city, country, weather description, and the timestamp of when the data was saved.
 */
@Data
@Entity
@Table(name = "weather_data")
public class WeatherData {

    /**
     * The unique identifier for the weather data record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the city (e.g., "London").
     */
    private String city;

    /**
     * The country code (e.g., "UK").
     */
    private String country;

    /**
     * The weather description (e.g., "clear sky", "rain").
     */
    private String description;

    /**
     * The timestamp indicating when the data was saved.
     */
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Default constructor required by JPA.
     */
    public WeatherData() {}

    /**
     * Parameterized constructor for creating a new WeatherData instance.
     *
     * @param city        The name of the city
     * @param country     The country code
     * @param description The weather description
     */
    public WeatherData(String city, String country, String description) {
        this.city = city;
        this.country = country;
        this.description = description;
    }
}