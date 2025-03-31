package com.weather.api.weatherapi.repository;

import com.weather.api.weatherapi.dto.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository for weather data operations using Spring Data JPA.
 * <p>
 * Provides:
 * <ul>
 *   <li>CRUD operations for {@link WeatherData}</li>
 *   <li>Custom query for database health checks</li>
 * </ul>
 *
 * @author YourName
 */
@Repository
public interface WeatherRepository extends JpaRepository<WeatherData, Long> {

    /**
     * Finds weather data by city and country (composite key).
     *
     * @param city    the city name (e.g., "London")
     * @param country the country code (e.g., "UK")
     * @return Optional containing the weather data, or empty if not found
     */
    Optional<WeatherData> findByCityAndCountry(String city, String country);

    /**
     * Verifies database connectivity by executing a simple query.
     * <p>
     * This is a lightweight check that returns 1 if the database is reachable.
     *
     * @return Integer result of the query (1 if successful)
     */
    @Query("SELECT 1 FROM WeatherData")
    Integer healthCheck();
}