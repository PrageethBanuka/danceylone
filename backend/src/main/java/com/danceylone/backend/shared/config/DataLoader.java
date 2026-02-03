package com.danceylone.backend.shared.config;

import com.danceylone.backend.user.domain.UserRepository;
import com.danceylone.backend.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
@Profile({"dev", "test"}) // Only run in dev and test profiles, not in production
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    CommandLineRunner load(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            // Check if admin user already exists to avoid duplicates
            if (repo.findByEmail("admin@danceylone.com").isEmpty()) {
                // Read admin password from environment variable
                String adminPassword = System.getenv("ADMIN_PASSWORD");
                
                // Validate that the password is set
                if (adminPassword == null || adminPassword.isBlank()) {
                    throw new IllegalStateException(
                        "ADMIN_PASSWORD environment variable must be set for data seeding. " +
                        "Please set a strong password (minimum 12 characters recommended)."
                    );
                }
                
                // Create admin user with the secure password from environment
                repo.save(new User(
                        UUID.randomUUID(),
                        "admin@danceylone.com",
                        encoder.encode(adminPassword),
                        "Admin",
                        "User"
                ));
                
                log.info("Admin user created successfully.");
            } else {
                log.info("Admin user already exists, skipping creation.");
            }
        };
    }
}