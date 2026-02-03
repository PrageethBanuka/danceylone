package com.danceylone.backend.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuthenticatedUser {

    private AuthenticatedUser() {}

    /**
     * Safely retrieves the user ID from the security context.
     * Returns Optional.empty() if authentication is null, principal is null, or principal is not a Long.
     */
    public static Optional<Long> getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if authentication exists
        if (auth == null) {
            return Optional.empty();
        }
        
        Object principal = auth.getPrincipal();
        
        // Check if principal exists and is of the expected type
        if (principal == null) {
            return Optional.empty();
        }
        
        // Safely cast to Long if possible using pattern matching
        if (principal instanceof Long userId) {
            return Optional.of(userId);
        }
        
        // Principal is not a Long (might be UserDetails or String)
        return Optional.empty();
    }
}