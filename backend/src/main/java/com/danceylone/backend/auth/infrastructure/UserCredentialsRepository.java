package com.danceylone.backend.auth.infrastructure;

import com.danceylone.backend.auth.domain.AuthUser;

import java.util.Optional;

public interface UserCredentialsRepository {
    Optional<AuthUser> findByEmail(String email);
}
