package com.danceylone.backend.shared.config;

import com.danceylone.backend.shared.constants.SecurityConstants;
import com.danceylone.backend.user.domain.Role;
import com.danceylone.backend.user.domain.UserRepository;
import com.danceylone.backend.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.UUID;

/**
 * Data Loader for Development and Test Environments
 * 
 * PRODUCTION BEST PRACTICE: Environment-specific initialization
 * - Only runs in dev/test profiles (NOT production)
 * - Uses constants for consistent configuration
 * - Secure password handling from environment variables
 * - Idempotent (safe to run multiple times)
 */
@Configuration
@Profile({"dev", "test"}) // Only run in dev and test profiles, not in production
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    CommandLineRunner load(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            // Check if admin user already exists to avoid duplicates (idempotent)
            if (repo.findByEmail(SecurityConstants.DEFAULT_ADMIN_EMAIL).isEmpty()) {
                // Read admin password from environment variable
                String adminPassword = System.getenv("ADMIN_PASSWORD");
                
                // Validate that the password is set
                if (adminPassword == null || adminPassword.isBlank()) {
                    throw new IllegalStateException(
                        "ADMIN_PASSWORD environment variable must be set for data seeding. " +
                        "Please set a strong password (minimum " + SecurityConstants.MIN_PASSWORD_LENGTH + " characters recommended)."
                    );
                }
                
                // Create admin user with ADMIN and USER roles
                User adminUser = new User(
                        UUID.randomUUID(),
                        SecurityConstants.DEFAULT_ADMIN_EMAIL,
                        encoder.encode(adminPassword),
                        SecurityConstants.DEFAULT_ADMIN_FIRST_NAME,
                        SecurityConstants.DEFAULT_ADMIN_LAST_NAME,
                        Set.of(Role.USER, Role.ADMIN)
                );
                
                repo.save(adminUser);
                
                log.info("Admin user created successfully with email: {}", SecurityConstants.DEFAULT_ADMIN_EMAIL);
            } else {
                log.info("Admin user already exists, skipping creation.");
            }
        };
    }
}