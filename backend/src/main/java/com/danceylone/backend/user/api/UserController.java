package com.danceylone.backend.user.api;

import com.danceylone.backend.user.api.dto.UserMeResponse;
import com.danceylone.backend.user.domain.User;
import com.danceylone.backend.user.domain.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public UserMeResponse me() {
        // Get the authenticated user's email from the JWT token (stored as principal/subject)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new NoSuchElementException("User not authenticated");
        }
        
        String email = authentication.getPrincipal().toString();
        
        // Look up user by email (JWT subject contains the email)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + email));

        return new UserMeResponse(user.getId(), user.getEmail());
    }
}