package com.danceylone.backend.auth.application;

import com.danceylone.backend.auth.api.dto.AuthResponse;
import com.danceylone.backend.shared.security.JwtTokenService;
import com.danceylone.backend.user.domain.UserRepository;
import com.danceylone.backend.user.domain.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public AuthResponse login(String email, String password) {
        // Fetch user by email
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        // Timing-attack mitigation: always perform password check even if user doesn't exist
        // Use a dummy hash to ensure constant-time behavior
        String hashToCheck = userOpt
                .map(User::getPasswordHash)
                .orElse("$2a$10$dummyHashToPreventTimingAttack1234567890123456789012");
        
        boolean passwordMatches = passwordEncoder.matches(password, hashToCheck);
        
        if (userOpt.isEmpty() || !passwordMatches) {
            throw new BadCredentialsException("Invalid credentials");
        }
        
        User user = userOpt.get();
        
        // Note: Using hardcoded "USER" role as the User entity currently doesn't have roles.
        // TODO: Once User entity is updated with roles, replace with: user.getRoles().stream().map(Role::name).collect(Collectors.toSet())
        String token = jwtTokenService.generateToken(
                user.getEmail(),
                Set.of("USER")
        );
        
        return AuthResponse.success(token, user.getEmail(), user.getFirstName(), user.getLastName());
    }

    @Override
    public User register(String email, String password, String firstName, String lastName) {
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Validate password strength
        validatePassword(password);

        // Create new user
        User newUser = new User(
                UUID.randomUUID(),
                email.toLowerCase().trim(),
                passwordEncoder.encode(password),
                firstName.trim(),
                lastName.trim()
        );

        return userRepository.save(newUser);
    }

    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        // Add more validation rules as needed
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one number");
        }
    }
}