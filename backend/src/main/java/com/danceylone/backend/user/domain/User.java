package com.danceylone.backend.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User Domain Entity - Phase 3 Enhanced
 * 
 * PRODUCTION PATTERN: Rich domain model with business logic
 * - Immutable by design (final fields)
 * - Validation in constructor
 * - Business methods (not just getters/setters)
 * 
 * PHASE 3 ADDITIONS:
 * - Account status management (active, locked, verified)
 * - Failed login tracking
 * - Email verification system
 * - Audit trail timestamps
 * 
 * INTERVIEW TALKING POINTS:
 * "User entity encapsulates all account state and business rules"
 * "Status fields enable account lifecycle management"
 * "Failed login tracking prevents brute force attacks"
 */
public class User {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final String firstName;
    private final String lastName;
    private final Set<Role> roles;
    
    // Phase 3: Account Status Fields
    private final boolean active;
    private final boolean emailVerified;
    private final boolean accountLocked;
    private final LocalDateTime lockedUntil;
    private final int failedLoginAttempts;
    private final LocalDateTime lastLoginAt;
    private final String emailVerificationToken;
    private final LocalDateTime emailVerificationSentAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /**
     * Constructor for new users (Phase 1 compatibility)
     */
    public User(UUID id, String email, String passwordHash, String firstName, String lastName) {
        this(id, email, passwordHash, firstName, lastName, Set.of(Role.USER));
    }

    /**
     * Constructor with roles (Phase 2 compatibility)
     */
    public User(UUID id, String email, String passwordHash, String firstName, String lastName, Set<Role> roles) {
        this(id, email, passwordHash, firstName, lastName, roles, 
             true, false, false, null, 0, null, null, null, 
             LocalDateTime.now(), LocalDateTime.now());
    }
    
    /**
     * Full constructor (Phase 3 - complete)
     * 
     * INTERVIEW: "Constructor validates all invariants"
     * "Ensures invalid user objects cannot be created"
     */
    public User(
            UUID id,
            String email,
            String passwordHash,
            String firstName,
            String lastName,
            Set<Role> roles,
            boolean active,
            boolean emailVerified,
            boolean accountLocked,
            LocalDateTime lockedUntil,
            int failedLoginAttempts,
            LocalDateTime lastLoginAt,
            String emailVerificationToken,
            LocalDateTime emailVerificationSentAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        
        // Validation
        if (id == null) throw new IllegalArgumentException("User ID cannot be null");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email cannot be null or empty");
        if (passwordHash == null || passwordHash.isBlank()) throw new IllegalArgumentException("Password hash cannot be null or empty");
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("First name cannot be null or empty");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("Last name cannot be null or empty");
        
        this.id = id;
        this.email = email.toLowerCase().trim();
        this.passwordHash = passwordHash;
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.roles = roles != null && !roles.isEmpty() ? Set.copyOf(roles) : Set.of(Role.USER);
        
        // Phase 3 fields
        this.active = active;
        this.emailVerified = emailVerified;
        this.accountLocked = accountLocked;
        this.lockedUntil = lockedUntil;
        this.failedLoginAttempts = Math.max(0, failedLoginAttempts);
        this.lastLoginAt = lastLoginAt;
        this.emailVerificationToken = emailVerificationToken;
        this.emailVerificationSentAt = emailVerificationSentAt;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Set<Role> getRoles() {
        return roles;
    }
    
    /**
     * Get role names as strings for serialization/storage
     */
    public Set<String> getRoleNames() {
        return roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
    }
    
    /**
     * Check if user has a specific role
     */
    public boolean hasRole(Role role) {
        return roles.contains(role);
    }
    
    /**
     * Check if user is an admin
     */
    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    /**
     * Returns the password hash.
     * WARNING: This should not be exposed in APIs - annotated with @JsonIgnore to prevent serialization.
     * Use verifyPassword() method instead for password verification.
     */
    @JsonIgnore
    public String getPasswordHash() {
        return passwordHash;
    }
    
    /**
     * Verifies if the provided plain text password matches the stored hash.
     * 
     * @param plainPassword the plain text password to verify
     * @param encoder the password encoder to use for verification
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String plainPassword, PasswordEncoder encoder) {
        return encoder.matches(plainPassword, this.passwordHash);
    }
    
    // ===== PHASE 3: ACCOUNT STATUS GETTERS =====
    
    public boolean isActive() { return active; }
    public boolean isEmailVerified() { return emailVerified; }
    public boolean isAccountLocked() { return accountLocked; }
    public LocalDateTime getLockedUntil() { return lockedUntil; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public String getEmailVerificationToken() { return emailVerificationToken; }
    public LocalDateTime getEmailVerificationSentAt() { return emailVerificationSentAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // ===== PHASE 3: BUSINESS LOGIC METHODS =====
    
    /**
     * Check if user can login
     * INTERVIEW: "Encapsulate business rules in domain model"
     * 
     * User cannot login if:
     * - Account is not active (deactivated by admin)
     * - Account is locked (security lockout)
     * - Email not verified (configurable per business rules)
     */
    public boolean canLogin() {
        if (!active) return false;
        if (accountLocked && isCurrentlyLocked()) return false;
        // Note: emailVerified check can be configurable
        // For now, allow login even if email not verified
        return true;
    }
    
    /**
     * Check if account is currently locked
     * INTERVIEW: "Temporary locks auto-expire"
     */
    public boolean isCurrentlyLocked() {
        if (!accountLocked) return false;
        if (lockedUntil == null) return true; // Permanent lock
        return LocalDateTime.now().isBefore(lockedUntil);
    }
    
    /**
     * Check if email verification token is still valid
     * INTERVIEW: "Tokens expire after 24 hours for security"
     */
    public boolean isEmailVerificationTokenValid() {
        if (emailVerificationToken == null || emailVerificationToken.isBlank()) return false;
        if (emailVerificationSentAt == null) return false;
        // Token valid for 24 hours
        LocalDateTime expiryTime = emailVerificationSentAt.plusHours(24);
        return LocalDateTime.now().isBefore(expiryTime);
    }
    
    /**
     * Check if account needs verification
     * INTERVIEW: "Business rule: Unverified accounts older than 7 days"
     */
    public boolean needsVerification() {
        if (emailVerified) return false;
        if (createdAt == null) return false;
        // Accounts created more than 7 days ago need verification
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return createdAt.isBefore(sevenDaysAgo);
    }
    
    /**
     * Check if account should be auto-locked due to failed attempts
     * INTERVIEW: "Security rule: Lock after 5 failed attempts"
     */
    public boolean shouldBeLocked() {
        return failedLoginAttempts >= 5;
    }
    
    /**
     * Copy method for immutable updates
     * INTERVIEW: "Immutability pattern - return new instance with changes"
     */
    public User withActivation(boolean newActive) {
        return new User(id, email, passwordHash, firstName, lastName, roles,
                newActive, emailVerified, accountLocked, lockedUntil, 
                failedLoginAttempts, lastLoginAt, emailVerificationToken, 
                emailVerificationSentAt, createdAt, LocalDateTime.now());
    }
    
    public User withEmailVerified(boolean verified) {
        return new User(id, email, passwordHash, firstName, lastName, roles,
                active, verified, accountLocked, lockedUntil, 
                failedLoginAttempts, lastLoginAt, null, // Clear token
                null, createdAt, LocalDateTime.now());
    }
    
    public User withAccountLocked(boolean locked, LocalDateTime until) {
        return new User(id, email, passwordHash, firstName, lastName, roles,
                active, emailVerified, locked, until, 
                failedLoginAttempts, lastLoginAt, emailVerificationToken, 
                emailVerificationSentAt, createdAt, LocalDateTime.now());
    }
    
    public User withFailedLoginAttempt() {
        return new User(id, email, passwordHash, firstName, lastName, roles,
                active, emailVerified, accountLocked, lockedUntil, 
                failedLoginAttempts + 1, lastLoginAt, emailVerificationToken, 
                emailVerificationSentAt, createdAt, LocalDateTime.now());
    }
    
    public User withSuccessfulLogin() {
        return new User(id, email, passwordHash, firstName, lastName, roles,
                active, emailVerified, accountLocked, lockedUntil, 
                0, // Reset failed attempts
                LocalDateTime.now(), // Update last login
                emailVerificationToken, emailVerificationSentAt, 
                createdAt, LocalDateTime.now());
    }
    
    public User withEmailVerificationToken(String token) {
        return new User(id, email, passwordHash, firstName, lastName, roles,
                active, emailVerified, accountLocked, lockedUntil, 
                failedLoginAttempts, lastLoginAt, token, 
                LocalDateTime.now(), // Token sent now
                createdAt, LocalDateTime.now());
    }
}