package com.danceylone.backend.shared.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Development-only security configuration for H2 console.
 * This configuration is only active when the 'dev' profile is enabled.
 */
@Configuration
@Profile("dev")
public class DevSecurityConfig {

    /**
     * Security filter chain specifically for H2 console in development.
     * Disables frame options and CSRF for H2 console access.
     */
    @Bean
    @Order(1) // Higher priority than the main security filter chain
    public SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/h2-console/**")
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
