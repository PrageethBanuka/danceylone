package com.danceylone.backend.user.infrastructure;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * User JPA Entity - Phase 3 Enhanced
 * 
 * INFRASTRUCTURE LAYER: Maps to database table
 * 
 * INTERVIEW: Explain Persistence vs Domain
 * "JPA entities are infrastructure concern - they map to DB"
 * "Domain entities contain business logic - no JPA annotations"
 * "Repository adapts between the two layers"
 */
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();
    
    // ===== PHASE 3: ACCOUNT STATUS FIELDS =====
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column(nullable = false)
    private boolean emailVerified = false;
    
    @Column(nullable = false)
    private boolean accountLocked = false;
    
    @Column
    private LocalDateTime lockedUntil;
    
    @Column(nullable = false)
    private int failedLoginAttempts = 0;
    
    @Column
    private LocalDateTime lastLoginAt;
    
    @Column(length = 255)
    private String emailVerificationToken;
    
    @Column
    private LocalDateTime emailVerificationSentAt;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * JPA requires no-arg constructor
     */
    protected UserEntity() {}

    /**
     * Constructor for Phase 1 compatibility
     */
    public UserEntity(UUID id, String email, String passwordHash, String firstName, String lastName) {
        this(id, email, passwordHash, firstName, lastName, Set.of("USER"));
    }

    /**
     * Constructor with roles (Phase 2 compatibility)
     */
    public UserEntity(UUID id, String email, String passwordHash, String firstName, String lastName, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>(Set.of("USER"));
        this.active = true;
        this.emailVerified = false;
        this.accountLocked = false;
        this.failedLoginAttempts = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Lifecycle callback: Set createdAt before persist
     * INTERVIEW: "JPA lifecycle callbacks for automatic timestamp management"
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Lifecycle callback: Update updatedAt before update
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ===== GETTERS AND SETTERS =====
    
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    
    // Phase 3 getters/setters
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    
    public boolean isAccountLocked() { return accountLocked; }
    public void setAccountLocked(boolean accountLocked) { this.accountLocked = accountLocked; }
    
    public LocalDateTime getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }
    
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
    
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    
    public String getEmailVerificationToken() { return emailVerificationToken; }
    public void setEmailVerificationToken(String emailVerificationToken) { 
        this.emailVerificationToken = emailVerificationToken; 
    }
    
    public LocalDateTime getEmailVerificationSentAt() { return emailVerificationSentAt; }
    public void setEmailVerificationSentAt(LocalDateTime emailVerificationSentAt) { 
        this.emailVerificationSentAt = emailVerificationSentAt; 
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}