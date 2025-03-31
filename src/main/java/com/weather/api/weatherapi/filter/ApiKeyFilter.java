package com.weather.api.weatherapi.filter;

import com.weather.api.weatherapi.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Filter to validate API keys for incoming requests.
 * <p>
 * This filter ensures that all requests (except excluded endpoints) include a valid API key.
 * If the API key is missing or invalid, an appropriate error response is returned.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

    private final ApiKeyService apiKeyService;

    /**
     * List of endpoints to exclude from API key validation.
     */
    private static final Set<String> EXCLUDED_ENDPOINTS = Set.of(
            "/api/weather/health", // Health check endpoint
            "/api/key",            // API key management endpoint
            "/api/key/**",         // API key management sub-endpoints
            "/swagger-ui.html",    // Main Swagger UI page
            "/swagger-ui",      // Additional Swagger resources
            "/v3/api-docs"      // OpenAPI JSON schema
    );

    /**
     * Filters incoming requests to validate API keys.
     *
     * @param request     The HTTP request
     * @param response    The HTTP response
     * @param filterChain The filter chain to proceed with the request
     * @throws ServletException If a servlet-related error occurs
     * @throws IOException      If an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("Processing request: URI={}, QueryString={}", request.getRequestURI(), request.getQueryString());

        // Skip API key validation for excluded endpoints
        if (isExcludedEndpoint(request.getRequestURI())) {
            log.debug("Skipping API key validation for excluded endpoint: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getParameter("apiKey");
        log.debug("Retrieved API Key: {}", apiKey);

        // Validate API key presence
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("Missing or empty API key in request.");
            response.sendError(HttpStatus.BAD_REQUEST.value(), "API key is required.");
            return;
        }

        // Validate API key validity
        if (apiKeyService.isValidKey(apiKey)) {
            log.debug("API Key is valid. Proceeding with the request.");
            filterChain.doFilter(request, response); // Pass the request to the next filter/controller
            return;
        }

        log.warn("Invalid API Key used: {}", apiKey);
        response.sendError(HttpStatus.FORBIDDEN.value(), "Invalid API key");
    }

    /**
     * Determines if the request URI should bypass API key validation.
     *
     * @param requestURI The request URI
     * @return True if the endpoint is excluded, false otherwise
     */
    private boolean isExcludedEndpoint(String requestURI) {
        return EXCLUDED_ENDPOINTS.stream()
                .anyMatch(requestURI::startsWith);
    }
}