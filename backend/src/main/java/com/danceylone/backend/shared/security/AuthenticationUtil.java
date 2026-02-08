package com.danceylone.backend.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class AuthenticationUtil {

    /**
     * Extract the authenticated user's ID from the security context.
     * The JWT filter sets the user ID as the authentication principal.
     *
     * @return The user ID from the JWT token
     * @throws IllegalStateException if user is not authenticated or user ID is invalid
     */
    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal == null || !(principal instanceof String)) {
            throw new IllegalStateException("Invalid authentication principal");
        }
        
        try {
            return UUID.fromString((String) principal);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid user ID format: " + principal, e);
        }
    }
    
    /**
     * Check if a user is currently authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}
