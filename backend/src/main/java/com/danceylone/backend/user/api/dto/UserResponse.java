package com.danceylone.backend.user.api.dto;

import java.util.List;
import java.util.UUID;

/**
 * User Response DTO
 * 
 * SECURITY: Never expose password hash
 * Only return safe user information
 */
public record UserResponse(
    UUID id,
    String email,
    String firstName,
    String lastName,
    List<String> roles,
    boolean active
) {}
