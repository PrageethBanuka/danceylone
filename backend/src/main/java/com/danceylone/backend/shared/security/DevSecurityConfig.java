package com.danceylone.backend.shared.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Development-only Security Configuration
 * 
 * ONLY ACTIVE IN DEV PROFILE
 * Permits public access to:
 * - Swagger UI and API documentation
 * - H2 Console
 * 
 * PRODUCTION NOTE:
 * This configuration is NOT loaded in production.
 * In production, Swagger endpoints require authentication.
 */
@Configuration
@Profile("dev")
public class DevSecurityConfig {

    /**
     * Security filter chain for development tools (H2 Console + Swagger).
     * Disables security for documentation and debugging tools in dev environment only.
     */
    @Bean
    @Order(1) // Higher priority than the main security filter chain
    public SecurityFilterChain devToolsSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/h2-console/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**")
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
