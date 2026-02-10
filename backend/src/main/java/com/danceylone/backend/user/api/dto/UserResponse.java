package com.danceylone.backend.user.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * User Response DTO (Phase 3 - Enhanced with status fields)
 * 
 * Interview Tip: DTOs (Data Transfer Objects) are for API communication only.
 * - They differ from domain models (separate concerns)
 * - Can flatten complex domain objects
 * - Never expose sensitive data (password hash, verification tokens)
 * - Add documentation for frontend developers
 * 
 * SECURITY: Never expose password hash or email verification tokens
 */
public record UserResponse(
    UUID id,
    String email,
    String firstName,
    String lastName,
    List<String> roles,
    boolean active,
    boolean emailVerified,
    boolean accountLocked,
    LocalDateTime lockedUntil,
    Integer failedLoginAttempts,
    LocalDateTime lastLoginAt,
    LocalDateTime createdAt
) {}
