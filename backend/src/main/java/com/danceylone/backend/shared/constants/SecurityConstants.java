package com.danceylone.backend.shared.constants;

/**
 * Security-related constants
 * 
 * PRODUCTION BEST PRACTICE: Centralize magic strings and numbers
 * 
 * Benefits:
 * - Single source of truth
 * - Easy to update globally
 * - Self-documenting code
 * - Prevents inconsistencies
 */
public final class SecurityConstants {
    
    // Prevent instantiation
    private SecurityConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // Password validation
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 128;
    public static final String PASSWORD_PATTERN_UPPERCASE = ".*[A-Z].*";
    public static final String PASSWORD_PATTERN_LOWERCASE = ".*[a-z].*";
    public static final String PASSWORD_PATTERN_DIGIT = ".*\\d.*";
    public static final String PASSWORD_PATTERN_SPECIAL = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*";
    
    // JWT
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final long JWT_EXPIRATION_MS = 3600000L; // 1 hour
    public static final int JWT_MIN_SECRET_LENGTH = 64; // 512 bits
    
    // CORS
    public static final String CORS_ALLOWED_ORIGIN = "http://localhost:3000";
    public static final long CORS_MAX_AGE = 3600L;
    
    // Rate limiting (for future implementation)
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final long LOGIN_ATTEMPT_WINDOW_MS = 900000L; // 15 minutes
    
    // Default admin credentials
    public static final String DEFAULT_ADMIN_EMAIL = "admin@danceylone.com";
    public static final String DEFAULT_ADMIN_FIRST_NAME = "Admin";
    public static final String DEFAULT_ADMIN_LAST_NAME = "User";
}
