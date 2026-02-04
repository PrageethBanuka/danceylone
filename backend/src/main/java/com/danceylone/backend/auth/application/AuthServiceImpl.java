package com.danceylone.backend.auth.application;

import com.danceylone.backend.auth.api.dto.AuthResponse;
import com.danceylone.backend.shared.constants.SecurityConstants;
import com.danceylone.backend.shared.constants.ValidationMessages;
import com.danceylone.backend.shared.exception.ValidationException;
import com.danceylone.backend.shared.security.JwtTokenService;
import com.danceylone.backend.user.domain.UserRepository;
import com.danceylone.backend.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Authentication Service Implementation
 * 
 * PRODUCTION IMPROVEMENTS:
 * - Uses constants instead of magic strings
 * - Custom exceptions with detailed messages
 * - Transaction management
 * - Comprehensive password validation
 * - Structured logging
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

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
    @Transactional(readOnly = true)
    public AuthResponse login(String email, String password) {
        log.debug("Login attempt for email: {}", email);
        
        // Fetch user by email
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            log.warn("Login failed - user not found: {}", email);
            throw new BadCredentialsException(ValidationMessages.INVALID_CREDENTIALS);
        }
        
        User user = userOpt.get();
        
        // Verify password
        if (!user.verifyPassword(password, passwordEncoder)) {
            log.warn("Login failed - invalid password for user: {}", email);
            throw new BadCredentialsException(ValidationMessages.INVALID_CREDENTIALS);
        }
        
        // Generate token with user's actual roles
        String token = jwtTokenService.generateToken(
                user.getEmail(),
                user.getRoleNames()
        );
        
        log.info("Login successful for user: {} with roles: {}", email, user.getRoleNames());
        
        return AuthResponse.success(
                token,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoleNames()
        );
    }

    @Override
    public User register(String email, String password, String firstName, String lastName) {
        log.debug("Registration attempt for email: {}", email);
        
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("Registration failed - email already exists: {}", email);
            throw new ValidationException("email", ValidationMessages.EMAIL_ALREADY_EXISTS);
        }

        // Validate password strength
        validatePassword(password);
        
        // Validate input fields
        validateRegistrationInput(email, firstName, lastName);

        // Create new user
        String encodedPassword = passwordEncoder.encode(password);
        
        User newUser = new User(
                UUID.randomUUID(),
                email.toLowerCase().trim(),
                encodedPassword,
                firstName.trim(),
                lastName.trim()
        );

        User saved = userRepository.save(newUser);
        log.info("User registered successfully: {}", email);
        
        return saved;
    }

    /**
     * Validate password meets security requirements
     * 
     * PRODUCTION BEST PRACTICE: Comprehensive password validation
     * - Enforces minimum length
     * - Requires mix of character types
     * - Prevents overly long passwords (DoS protection)
     */
    private void validatePassword(String password) {
        if (password == null || password.length() < SecurityConstants.MIN_PASSWORD_LENGTH) {
            throw new ValidationException("password", ValidationMessages.PASSWORD_TOO_SHORT);
        }
        
        if (password.length() > SecurityConstants.MAX_PASSWORD_LENGTH) {
            throw new ValidationException("password", ValidationMessages.PASSWORD_TOO_LONG);
        }
        
        if (!password.matches(SecurityConstants.PASSWORD_PATTERN_UPPERCASE)) {
            throw new ValidationException("password", ValidationMessages.PASSWORD_NO_UPPERCASE);
        }
        
        if (!password.matches(SecurityConstants.PASSWORD_PATTERN_LOWERCASE)) {
            throw new ValidationException("password", ValidationMessages.PASSWORD_NO_LOWERCASE);
        }
        
        if (!password.matches(SecurityConstants.PASSWORD_PATTERN_DIGIT)) {
            throw new ValidationException("password", ValidationMessages.PASSWORD_NO_DIGIT);
        }
    }
    
    /**
     * Validate registration input fields
     */
    private void validateRegistrationInput(String email, String firstName, String lastName) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("email", ValidationMessages.EMAIL_REQUIRED);
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("email", ValidationMessages.EMAIL_INVALID);
        }
        
        if (firstName == null || firstName.isBlank()) {
            throw new ValidationException("firstName", "First name is required");
        }
        
        if (lastName == null || lastName.isBlank()) {
            throw new ValidationException("lastName", "Last name is required");
        }
    }
}