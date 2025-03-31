package com.weather.api.weatherapi.service;

import com.weather.api.weatherapi.exception.RateLimitExceededException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service to enforce rate limiting (5 requests/hour per API key).
 * <p>
 * This service tracks the number of requests made by each API key and enforces a limit of 5 requests per hour.
 * The request counts are reset every hour using a scheduled task.
 */
@Slf4j
@Service
public class RateLimitService {

    private static final int MAX_REQUESTS_PER_HOUR = 5;

    /**
     * A thread-safe map to store request counts for each API key.
     * - Key: API key
     * - Value: AtomicInteger representing the number of requests made by the API key.
     */
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();

    /**
     * Validates if the API key has exceeded the rate limit.
     *
     * @param apiKey the API key to validate
     * @throws RateLimitExceededException if the API key exceeds the allowed number of requests per hour
     */
    public void validateRateLimit(String apiKey) {
        // Get or initialize the request count for the API key
        AtomicInteger count = requestCounts.computeIfAbsent(apiKey, k -> new AtomicInteger(0));

        // Increment the count and check if it exceeds the limit
        if (count.incrementAndGet() > MAX_REQUESTS_PER_HOUR) {
            log.warn("Rate limit exceeded for API Key: {}", apiKey);
            throw new RateLimitExceededException();
        }

        log.info("Request count for API Key {}: {}", apiKey, count.get());
    }

    /**
     * Resets the request counts every hour.
     */
    @Scheduled(fixedRate = 3600000) // 3600000 ms = 1 hour
    public void resetCounts() {
        log.info("Resetting rate limit counts.");
        requestCounts.clear();
    }
}