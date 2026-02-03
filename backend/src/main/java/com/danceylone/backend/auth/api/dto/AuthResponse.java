package com.danceylone.backend.auth.api.dto;

public record AuthResponse(
        String token,
        String email,
        String firstName,
        String lastName,
        String message
) {
    public static AuthResponse success(String token, String email, String firstName, String lastName) {
        return new AuthResponse(token, email, firstName, lastName, "Login successful");
    }

    public static AuthResponse registered(String email, String firstName) {
        return new AuthResponse(null, email, firstName, null, "Registration successful");
    }
}
