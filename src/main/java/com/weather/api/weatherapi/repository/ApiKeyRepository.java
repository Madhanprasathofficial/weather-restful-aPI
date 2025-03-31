package com.weather.api.weatherapi.repository;

import com.weather.api.weatherapi.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing API keys in the database.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations
 * and additional query methods for the {@link ApiKey} entity.
 */
@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    /**
     * Retrieves all API keys from the database.
     *
     * @return A list of all API keys
     */
    List<ApiKey> findAll();
}