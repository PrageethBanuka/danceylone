package com.danceylone.backend.user.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * User Repository - Domain Layer
 * 
 * MODULAR MONOLITH: This interface belongs to the User module's domain
 * DDD PATTERN: Repository abstracts persistence concerns
 * 
 * WHY PAGINATION IN REPOSITORY?
 * - Domain layer decides "what" data we need
 * - Infrastructure layer decides "how" to fetch it
 * - Clean separation of concerns
 */
public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    List<User> findAll();
    
    /**
     * Find users with pagination and sorting
     * PRODUCTION: Essential for large datasets
     */
    Page<User> findAll(Pageable pageable);
    
    /**
     * Search users by email or name with pagination
     * PRODUCTION: Search + Pagination = Scalable UX
     */
    Page<User> searchUsers(String searchTerm, Pageable pageable);
    
    /**
     * Filter users by role with pagination
     * PRODUCTION: Role-based filtering for admin management
     */
    Page<User> findByRole(String role, Pageable pageable);
    
    User save(User user);
}