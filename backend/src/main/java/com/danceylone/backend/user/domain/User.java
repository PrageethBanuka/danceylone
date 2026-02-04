package com.danceylone.backend.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User Domain Entity
 * 
 * PRODUCTION PATTERN: Rich domain model with business logic
 * - Immutable by design (final fields)
 * - Validation in constructor
 * - Business methods (not just getters/setters)
 */
public class User {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final String firstName;
    private final String lastName;
    private final Set<Role> roles;

    public User(UUID id, String email, String passwordHash, String firstName, String lastName) {
        this(id, email, passwordHash, firstName, lastName, Set.of(Role.USER));
    }

    public User(UUID id, String email, String passwordHash, String firstName, String lastName, Set<Role> roles) {
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
}