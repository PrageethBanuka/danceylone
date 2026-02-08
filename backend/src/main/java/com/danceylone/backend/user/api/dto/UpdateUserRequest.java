package com.danceylone.backend.user.api.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Update User Request DTO
 * For updating user roles and status
 */
public record UpdateUserRequest(
    @NotEmpty(message = "Roles cannot be empty")
    List<String> roles
) {}
