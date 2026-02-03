package com.danceylone.backend.auth.domain;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class AuthUser {
    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final Set<Role> roles;

    public AuthUser(UUID id, String email, String passwordHash, Set<Role> roles) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        // Defensive copy: protect against external mutation, handle null as empty set
        this.roles = roles == null ? Set.of() : Set.copyOf(roles);
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Set<Role> getRoles() {
        // Return unmodifiable view to prevent external mutation
        return Collections.unmodifiableSet(roles);
    }
}
