package com.weather.api.weatherapi.config;


import com.weather.api.weatherapi.filter.ApiKeyFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // Add this to inject ApiKeyFilter
public class SecurityConfig {

    private final ApiKeyFilter apiKeyFilter; // Inject the filter

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity
                .authorizeHttpRequests(auth -> auth
                        // Permit access to excluded endpoints
                        .requestMatchers("/api/weather/health").permitAll()
                        .requestMatchers("/api/key").permitAll()
                        .requestMatchers("/api/key/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui").permitAll()
                        .requestMatchers("/v3/api-docs").permitAll()
                        .requestMatchers("/api/weather/getWeather").permitAll() // Permit the /getWeather endpoint
                        // Secure all other endpoints
                        .anyRequest().authenticated()
                )
                // Add the API key filter
                .addFilterBefore(apiKeyFilter, BasicAuthenticationFilter.class);

        return http.build();
    }
}