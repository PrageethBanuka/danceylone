package com.danceylone.backend.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public class User {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final String firstName;
    private final String lastName;

    public User(UUID id, String email, String passwordHash, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
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