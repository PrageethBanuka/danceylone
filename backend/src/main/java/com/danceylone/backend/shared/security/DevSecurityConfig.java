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
 * - Admin endpoints (for UI development/testing)
 * 
 * PRODUCTION NOTE:
 * This configuration is NOT loaded in production.
 * In production, all admin endpoints require authentication and ADMIN role.
 * 
 * INTERVIEW TIP: Explain dev vs production security
 * "In dev, we disable auth for faster testing"
 * "In production, full OAuth2/JWT + RBAC is enforced"
 * "This is controlled by Spring profiles - @Profile('dev')"
 */
@Configuration
@Profile("dev")
public class DevSecurityConfig {

    /**
     * Security filter chain for development tools and admin testing.
     * 
     * SECURITY LAYERS IN PRODUCTION:
     * 1. JWT Authentication (validates token)
     * 2. Role-Based Access Control (checks ADMIN role)
     * 3. Method-level security (@PreAuthorize)
     * 4. Data-level security (users can only see their own data)
     * 
     * IN DEV: We bypass 1-2 for easier frontend development
     * BUT: Always test with real auth before production!
     */
    @Bean
    @Order(1) // Higher priority than the main security filter chain
    public SecurityFilterChain devToolsSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                    "/h2-console/**", 
                    "/swagger-ui.html", 
                    "/swagger-ui/**", 
                    "/v3/api-docs/**", 
                    "/api-docs/**",
                    "/api/users/**",     // Admin user management (dev only!)
                    "/api/orders/**"     // Admin order management (dev only!)
                )
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
