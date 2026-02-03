package com.danceylone.backend.auth.application;

import com.danceylone.backend.auth.api.dto.AuthResponse;
import com.danceylone.backend.user.domain.User;

public interface AuthService {
    AuthResponse login(String email, String password);
    User register(String email, String password, String firstName, String lastName);
}
