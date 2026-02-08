package com.danceylone.backend.shared.security;

import com.danceylone.backend.shared.config.JwtProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Main Security Configuration
 * 
 * SPRING SECURITY ARCHITECTURE:
 * 1. SecurityFilterChain - defines security rules
 * 2. JwtAuthenticationFilter - validates JWT tokens
 * 3. CORS Configuration - allows cross-origin requests
 * 4. Authorization rules - who can access what
 * 
 * INTERVIEW TIP: Explain stateless authentication
 * "JWT tokens make sessions stateless - no server-side session storage"
 * "Each request includes token, validated independently"
 * "Scales horizontally - any server can validate any token"
 */
@Configuration
public class SecurityConfig {

    private final JwtProperties jwtProperties;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(
            JwtProperties jwtProperties,
            CorsConfigurationSource corsConfigurationSource) {
        this.jwtProperties = jwtProperties;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationFilter jwtFilter =
                new JwtAuthenticationFilter(jwtProperties.getSecret());

        http
                // CORS: Use our comprehensive configuration from CorsConfig.java
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                
                // CSRF: Disabled for stateless REST APIs (JWT-based)
                .csrf(csrf -> csrf.disable())
                
                // Form Login: Disabled (using JWT, not sessions)
                .formLogin(form -> form.disable())
                
                // HTTP Basic: Disabled (using JWT Bearer tokens)
                .httpBasic(basic -> basic.disable())
                
                // SESSION MANAGEMENT: Stateless (no server-side sessions)
                // INTERVIEW: "JWT tokens eliminate server session storage"
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // AUTHORIZATION RULES
                // Order matters: specific rules before general rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()     // Login/Register
                        .requestMatchers("/api/products/**").permitAll() // Public catalog
                        .requestMatchers("/h2-console/**").permitAll()   // Dev only (H2)
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // JWT FILTER: Validate tokens before Spring Security processes request
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    /**
     * Password Encoder Bean
     * 
     * BCRYPT HASHING:
     * - Industry standard for password hashing
     * - Includes salt (random data to prevent rainbow table attacks)
     * - Configurable cost factor (higher = slower = more secure)
     * 
     * INTERVIEW: "Never store plaintext passwords"
     * "BCrypt is one-way hash - can't reverse it"
     * "Each password gets unique salt - same password = different hash"
     * 
     * SECURITY CONCEPTS SUMMARY (For Interviews):
     * 
     * 1. CSRF (Cross-Site Request Forgery)
     *    - Attack: Malicious site tricks user into submitting form
     *    - Protection: CSRF tokens
     *    - REST APIs: Disabled (use JWT instead)
     * 
     * 2. CORS (Cross-Origin Resource Sharing)
     *    - Browser blocks requests between different origins
     *    - We explicitly allow our frontend domain
     * 
     * 3. Stateless Authentication (JWT)
     *    - No server-side sessions
     *    - Token contains all needed info
     *    - Scales horizontally
     * 
     * 4. BCrypt Password Hashing
     *    - One-way encryption
     *    - Built-in salt
     *    - Configurable security level
     */
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}

