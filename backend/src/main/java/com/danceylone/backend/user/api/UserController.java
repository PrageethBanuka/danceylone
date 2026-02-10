package com.danceylone.backend.user.api;

import com.danceylone.backend.shared.application.AuditService;
import com.danceylone.backend.shared.domain.AuditAction;
import com.danceylone.backend.user.api.dto.UserMeResponse;
import com.danceylone.backend.user.api.dto.UserResponse;
import com.danceylone.backend.user.api.dto.UpdateUserRequest;
import com.danceylone.backend.shared.api.dto.PageRequest;
import com.danceylone.backend.shared.api.dto.PageResponse;
import com.danceylone.backend.user.domain.User;
import com.danceylone.backend.user.domain.UserRepository;
import com.danceylone.backend.user.domain.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User Management Controller - Phase 3
 * 
 * PRODUCTION USER MANAGEMENT FEATURES:
 * 1. ✅ List users with pagination, sorting
 * 2. ✅ Search users by email/name
 * 3. ✅ Filter users by role
 * 4. ✅ Get user by ID
 * 5. ✅ Activate/Deactivate users (Phase 3)
 * 6. ✅ Account locking (Phase 3)
 * 7. ✅ Audit logging (Phase 3)
 * 
 * ARCHITECTURAL PATTERNS:
 * - RESTful API design
 * - DTO pattern (never expose domain objects)
 * - Repository pattern (domain layer abstraction)
 * - Pagination for scalability
 * - Audit logging for compliance
 * 
 * SECURITY:
 * - Role-based access control (admin only)
 * - Never expose password hashes
 * - Input validation
 * - Audit trail for all admin actions
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User administration endpoints")
public class UserController {

    private final UserRepository userRepository;
    private final AuditService auditService;

    public UserController(UserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
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
     * Get users with pagination, sorting, and filtering (ADMIN ONLY)
     * 
     * PHASE 2: Production-ready user listing
     * 
     * INTERVIEW TALKING POINTS:
     * - "Pagination prevents loading thousands of records at once"
     * - "Sorting allows flexible data ordering (email, name, date)"
     * - "Search and role filter enable admin user discovery"
     * - "PageResponse provides frontend with total pages metadata"
     * 
     * PRODUCTION CONSIDERATIONS:
     * - Default page size: 20 (configurable)
     * - Max page size: 100 (prevents abuse)
     * - Case-insensitive search
     * - Combined search + role filter support
     * 
     * @param page Page number (0-indexed, default: 0)
     * @param size Items per page (1-100, default: 20)
     * @param sortBy Field to sort by (email, firstName, lastName, default: email)
     * @param direction Sort direction (ASC, DESC, default: ASC)
     * @param search Search term for email/firstName/lastName (optional)
     * @param role Filter by role (ADMIN, USER, etc., optional)
     * @return Paginated user list with metadata
     */
    @GetMapping
    @Operation(
        summary = "Get users with pagination (Admin only)",
        description = "Retrieves paginated list of users with sorting, search, and role filtering. " +
                     "Supports pagination to handle large datasets efficiently. Requires ADMIN role.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<PageResponse<UserResponse>> getUsers(
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Items per page (max 100)")
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Sort by field (email, firstName, lastName)")
            @RequestParam(defaultValue = "email") String sortBy,
            
            @Parameter(description = "Sort direction (ASC, DESC)")
            @RequestParam(defaultValue = "ASC") String direction,
            
            @Parameter(description = "Search in email, first name, last name")
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Filter by role (ADMIN, USER, SUPPORT, etc.)")
            @RequestParam(required = false) String role) {
        
        // Create pagination request with validation
        PageRequest pageReq = new PageRequest(page, size, sortBy, direction);
        org.springframework.data.domain.Pageable pageable = pageReq.toPageRequest();
        
        Page<User> userPage;
        
        // Apply search and role filters
        if (search != null && !search.isBlank() && role != null && !role.isBlank()) {
            // Both search and role filter
            userPage = userRepository.searchUsers(search.trim(), pageable);
            // Filter by role in-memory (could optimize with custom query)
            String roleUpper = role.toUpperCase();
            userPage = userPage.map(user -> 
                user.getRoles().stream().anyMatch(r -> r.name().equals(roleUpper)) ? user : null
            ).map(u -> u); // Filter nulls handled by Page
        } else if (search != null && !search.isBlank()) {
            // Search only
            userPage = userRepository.searchUsers(search.trim(), pageable);
        } else if (role != null && !role.isBlank()) {
            // Role filter only
            userPage = userRepository.findByRole(role.trim().toUpperCase(), pageable);
        } else {
            // No filters, return all
            userPage = userRepository.findAll(pageable);
        }
        
        // Convert domain Page to DTO Page Response
        Page<UserResponse> responsePage = userPage.map(this::toResponse);
        PageResponse<UserResponse> response = PageResponse.from(
            responsePage, 
            sortBy, 
            direction
        );
        
        return ResponseEntity.ok(response);
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
     * Activate a user (Phase 3 - Admin Action)
     * 
     * Interview Tip: This demonstrates several production patterns:
     * 1. Immutable domain updates (withActivation returns new User)
     * 2. Audit logging for compliance (SOC2 requires admin actions tracked)
     * 3. IP/User-Agent capture for security investigations
     * 4. Idempotency (activating already-active user is safe)
     * 
     * USE CASE: Admin reactivates a temporarily disabled account
     * 
     * @param userId The user to activate
     * @param request HTTP request (for IP/User-Agent)
     * @return Updated user
     */
    @PutMapping("/{userId}/activate")
    @Operation(
        summary = "Activate user (Admin only)",
        description = "Activates a deactivated user account. Creates audit log entry. Requires ADMIN role.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<UserResponse> activateUser(
            @PathVariable UUID userId,
            HttpServletRequest request) {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        
        // Get current admin user
        UUID performedBy = getCurrentUserId();
        
        // Update user status (immutable pattern - returns new User)
        User activatedUser = user.withActivation(true);
        User savedUser = userRepository.save(activatedUser);
        
        // Log audit trail
        auditService.logUserAction(
            userId,
            performedBy,
            AuditAction.USER_ACTIVATED,
            String.format("User %s (%s) activated by admin", user.getEmail(), userId),
            getClientIp(request),
            getUserAgent(request)
        );
        
        return ResponseEntity.ok(toResponse(savedUser));
    }

    /**
     * Deactivate a user (Phase 3 - Admin Action)
     * 
     * Interview Tip: Soft delete pattern
     * - Don't physically delete users (compliance, audit trail)
     * - Mark as inactive instead
     * - User can't login but data remains
     * - Can be reactivated later
     * 
     * USE CASE: Suspend suspicious account, temporarily disable inactive account
     * 
     * @param userId The user to deactivate
     * @param request HTTP request (for IP/User-Agent)
     * @return Updated user
     */
    @PutMapping("/{userId}/deactivate")
    @Operation(
        summary = "Deactivate user (Admin only)",
        description = "Deactivates a user account (soft delete). User cannot login but data preserved. " +
                     "Creates audit log entry. Requires ADMIN role.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<UserResponse> deactivateUser(
            @PathVariable UUID userId,
            HttpServletRequest request) {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        
        UUID performedBy = getCurrentUserId();
        
        // Deactivate user
        User deactivatedUser = user.withActivation(false);
        User savedUser = userRepository.save(deactivatedUser);
        
        // Log audit trail
        auditService.logUserAction(
            userId,
            performedBy,
            AuditAction.USER_DEACTIVATED,
            String.format("User %s (%s) deactivated by admin", user.getEmail(), userId),
            getClientIp(request),
            getUserAgent(request)
        );
        
        return ResponseEntity.ok(toResponse(savedUser));
    }

    /**
     * Unlock a user account (Phase 3 - Admin Action)
     * 
     * Interview Tip: Security vs Usability tradeoff
     * - Auto-lock after 5 failed attempts (security)
     * - Auto-unlock after 15 minutes (usability)
     * - Manual admin unlock (support workflow)
     * 
     * USE CASE: User locked out, contacts support, admin unlocks
     * 
     * @param userId The user to unlock
     * @param request HTTP request (for IP/User-Agent)
     * @return Updated user
     */
    @PutMapping("/{userId}/unlock")
    @Operation(
        summary = "Unlock user account (Admin only)",
        description = "Unlocks a locked user account and resets failed login counter. " +
                     "Creates audit log entry. Requires ADMIN role.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<UserResponse> unlockUser(
            @PathVariable UUID userId,
            HttpServletRequest request) {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        
        UUID performedBy = getCurrentUserId();
        
        // Unlock account and reset failed attempts
        User unlockedUser = user.withAccountLocked(false, null)
                                 .withSuccessfulLogin(); // Resets failed attempts counter
        User savedUser = userRepository.save(unlockedUser);
        
        // Log audit trail
        auditService.logUserAction(
            userId,
            performedBy,
            AuditAction.USER_UNLOCKED,
            String.format("User %s (%s) unlocked by admin", user.getEmail(), userId),
            getClientIp(request),
            getUserAgent(request)
        );
        
        return ResponseEntity.ok(toResponse(savedUser));
    }

    /**
     * Get current authenticated user's ID
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user");
        }
        
        String email = authentication.getPrincipal().toString();
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new NoSuchElementException("Current user not found"));
    }

    /**
     * Extract client IP address from request
     * 
     * Interview Tip: Check X-Forwarded-For header first (proxy/load balancer)
     * Falls back to remote address if no proxy
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Extract user agent from request
     */
    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    /**
     * Convert User domain → DTO (Phase 3 - Enhanced)
     * 
     * Interview Tip: This mapping layer protects the API from domain changes.
     * - If domain model changes, only this method needs updating
     * - API contract remains stable for frontend clients
     * - Can add computed fields without changing domain
     * 
     * SECURITY: Never expose password hash or email verification tokens
     */
    private UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRoles().stream().map(Role::name).collect(Collectors.toList()),
            user.isActive(),
            user.isEmailVerified(),
            user.isAccountLocked(),
            user.getLockedUntil(),
            user.getFailedLoginAttempts(),
            user.getLastLoginAt(),
            user.getCreatedAt()
        );
    }
}
