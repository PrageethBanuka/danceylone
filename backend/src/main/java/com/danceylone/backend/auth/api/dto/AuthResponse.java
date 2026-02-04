package com.danceylone.backend.auth.api.dto;

import java.util.Set;

public record AuthResponse(
        String token,
        String email,
        String firstName,
        String lastName,
        Set<String> roles,
        String message
) {
    public static AuthResponse success(String token, String email, String firstName, String lastName, Set<String> roles) {
        return new AuthResponse(token, email, firstName, lastName, roles, "Login successful");
    }

    public static AuthResponse registered(String email, String firstName) {
        return new AuthResponse(null, email, firstName, null, null, "Registration successful");
    }
}
