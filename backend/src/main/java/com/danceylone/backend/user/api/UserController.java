package com.danceylone.backend.user.api;

import com.danceylone.backend.user.api.dto.UserMeResponse;
import com.danceylone.backend.user.api.dto.UserResponse;
import com.danceylone.backend.user.api.dto.UpdateUserRequest;
import com.danceylone.backend.user.domain.User;
import com.danceylone.backend.user.domain.UserRepository;
import com.danceylone.backend.user.domain.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User Management Controller - Phase 1
 * 
 * PRODUCTION USER MANAGEMENT FEATURES:
 * 1. ✅ List all users (admin only)
 * 2. ✅ Get user by ID
 * 3. ✅ Search/filter users by role
 * 
 * SECURITY:
 * - Role-based access control (admin only)
 * - Never expose password hashes
 * - Input validation
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User administration endpoints")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get current authenticated user
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public UserMeResponse me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new NoSuchElementException("User not authenticated");
        }
        
        String email = authentication.getPrincipal().toString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + email));

        return new UserMeResponse(user.getId(), user.getEmail());
    }

    /**
     * Get all users (ADMIN ONLY)
     * 
     * PHASE 1: Basic listing with search and role filter
     * Future: Add pagination, sorting, advanced filters
     */
    @GetMapping
    @Operation(
        summary = "Get all users (Admin only)",
        description = "Retrieves list of all users with optional search and role filtering. Requires ADMIN role.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role) {
        
        List<User> users = userRepository.findAll();
        
        // Filter by search term (email or name)
        if (search != null && !search.isBlank()) {
            String searchLower = search.toLowerCase();
            users = users.stream()
                .filter(u -> u.getEmail().toLowerCase().contains(searchLower) ||
                            u.getFirstName().toLowerCase().contains(searchLower) ||
                            u.getLastName().toLowerCase().contains(searchLower))
                .collect(Collectors.toList());
        }
        
        // Filter by role
        if (role != null && !role.isBlank()) {
            try {
                Role roleEnum = Role.valueOf(role.toUpperCase());
                users = users.stream()
                    .filter(u -> u.hasRole(roleEnum))
                    .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Invalid role, ignore filter
            }
        }
        
        List<UserResponse> responses = users.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(responses);
    }

    /**
     * Get user by ID (ADMIN ONLY)
     */
    @GetMapping("/{userId}")
    @Operation(
        summary = "Get user by ID (Admin only)",
        description = "Retrieves detailed information about a specific user. Requires ADMIN role.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        return ResponseEntity.ok(toResponse(user));
    }

    /**
     * Convert User domain → DTO
     * SECURITY: Never expose password hash
     */
    private UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRoles().stream().map(Role::name).collect(Collectors.toList()),
            true // Phase 1: All users are active (add status field in Phase 3)
        );
    }
}
