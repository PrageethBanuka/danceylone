package com.danceylone.backend.auth.api;

import com.danceylone.backend.auth.api.dto.AuthResponse;
import com.danceylone.backend.auth.api.dto.LoginRequest;
import com.danceylone.backend.auth.api.dto.RegisterRequest;
import com.danceylone.backend.auth.application.AuthService;
import com.danceylone.backend.user.domain.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName()
        );
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AuthResponse.registered(user.getEmail(), user.getFirstName()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request.email(), request.password());
        return ResponseEntity.ok(response);
    }
}
