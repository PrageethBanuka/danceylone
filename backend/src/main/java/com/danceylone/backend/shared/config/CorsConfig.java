package com.danceylone.backend.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration - Cross-Origin Resource Sharing
 * 
 * WHAT IS CORS?
 * Browser security feature that blocks requests between different origins
 * Origin = Protocol + Domain + Port (http://localhost:3000 vs http://localhost:8080)
 * 
 * WHY DO WE NEED THIS?
 * - Frontend (React/Next.js): localhost:3000
 * - Backend (Spring Boot): localhost:8080
 * - Different ports = Different origins = CORS blocks by default
 * 
 * INTERVIEW TIP: Explain the security model
 * "CORS prevents malicious websites from making unauthorized requests to our API"
 * "We explicitly allow our frontend domain while blocking others"
 * "In production, we only allow our actual domain (e.g., https://danceylone.com)"
 * 
 * PRODUCTION SECURITY:
 * - Development: Allow localhost:3000 for local testing
 * - Production: Only allow https://yourdomain.com
 * - Use environment variables to configure allowed origins
 * 
 * PREFLIGHT REQUESTS:
 * Before actual request, browser sends OPTIONS request to check if allowed
 * We configure max age to cache this check (reduce overhead)
 */
@Configuration
public class CorsConfig {

    /**
     * Configure CORS for the application
     * 
     * INTERVIEW TALKING POINTS:
     * 1. AllowedOrigins: Which domains can call our API
     * 2. AllowedMethods: Which HTTP methods (GET, POST, PUT, DELETE)
     * 3. AllowedHeaders: Which headers frontend can send
     * 4. AllowCredentials: Can send cookies/auth tokens
     * 5. MaxAge: How long browser caches preflight response
     * 
     * PRODUCTION PATTERN: Use configuration properties
     * In real apps, allowed origins come from application.yml
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        /**
         * ALLOWED ORIGINS
         * Development: localhost:3000 (Next.js dev server)
         * Production: Add your actual domain
         * 
         * BEST PRACTICE: Load from environment variable
         * String allowedOrigin = System.getenv("FRONTEND_URL");
         */
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",      // Next.js dev server
            "http://localhost:3001",      // Alternative port
            "http://127.0.0.1:3000"       // Alternative localhost
            // In production, add: "https://danceylone.com"
        ));
        
        /**
         * ALLOWED METHODS
         * Specify which HTTP methods are allowed
         */
        configuration.setAllowedMethods(Arrays.asList(
            "GET",      // Read data
            "POST",     // Create data
            "PUT",      // Update data (full)
            "PATCH",    // Update data (partial)
            "DELETE",   // Delete data
            "OPTIONS"   // Preflight requests
        ));
        
        /**
         * ALLOWED HEADERS
         * Headers that frontend can send
         * 
         * IMPORTANT HEADERS:
         * - Authorization: JWT tokens
         * - Content-Type: JSON/form data
         * - X-Requested-With: AJAX identifier
         */
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",     // JWT Bearer tokens
            "Content-Type",      // application/json, etc.
            "X-Requested-With",  // XMLHttpRequest identifier
            "Accept",            // Response format
            "Origin",            // Request origin
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        /**
         * EXPOSE HEADERS
         * Headers that frontend JavaScript can access
         * By default, only simple headers are exposed
         */
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",             // Return new JWT tokens
            "Content-Disposition",       // File download names
            "X-Total-Count"             // Pagination metadata
        ));
        
        /**
         * ALLOW CREDENTIALS
         * true = Allow cookies, HTTP auth, SSL certificates
         * Required for JWT tokens in Authorization header
         * 
         * SECURITY NOTE: When true, cannot use allowedOrigins("*")
         * Must specify exact origins
         */
        configuration.setAllowCredentials(true);
        
        /**
         * MAX AGE
         * How long (seconds) browser caches preflight response
         * 3600 = 1 hour (reduce OPTIONS requests overhead)
         * 
         * INTERVIEW: Explain preflight caching
         * "Browser sends OPTIONS before actual request to check CORS"
         * "Caching this for 1 hour improves performance"
         */
        configuration.setMaxAge(3600L);
        
        /**
         * APPLY TO ALL ENDPOINTS
         * Register CORS configuration for all URL patterns
         */
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
