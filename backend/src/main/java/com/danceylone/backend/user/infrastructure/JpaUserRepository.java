package com.danceylone.backend.user.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA User Repository - Infrastructure Layer
 * 
 * SPRING DATA JPA MAGIC: Method name → SQL query
 * 
 * findByEmailContaining → WHERE email LIKE '%?%'
 * IgnoreCase → LOWER(email) LIKE LOWER('%?%')
 * Or → Combines multiple conditions with OR
 * 
 * INTERVIEW TIP: Spring Data JPA query derivation
 * - No SQL needed, just follow naming convention
 * - Findby + Property + Condition (Containing, IgnoreCase)
 * - Pageable parameter adds LIMIT/OFFSET automatically
 */
interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    
    /**
     * Search users by email, first name, or last name
     * PRODUCTION: Case-insensitive search across multiple fields
     */
    Page<UserEntity> findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String email, String firstName, String lastName, Pageable pageable);
    
    /**
     * Find users by role
     * PRODUCTION: Filter users by specific role
     * Note: roles is a Set<String>, Spring Data handles collection search
     */
    Page<UserEntity> findByRolesContaining(String role, Pageable pageable);
}