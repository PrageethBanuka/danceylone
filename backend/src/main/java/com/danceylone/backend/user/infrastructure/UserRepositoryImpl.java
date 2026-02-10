package com.danceylone.backend.user.infrastructure;

import com.danceylone.backend.user.domain.Role;
import com.danceylone.backend.user.domain.UserRepository;
import com.danceylone.backend.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User Repository Implementation - Adapter Pattern
 * 
 * PRODUCTION PATTERN: Maps between domain (User) and persistence (UserEntity)
 * - Keeps domain layer clean from JPA annotations
 * - Allows domain model to focus on business logic
 * - Easy to switch persistence technologies
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaRepo;

    public UserRepositoryImpl(JpaUserRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepo.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepo.findById(id)
                .map(this::toDomain);
    }

    @Override
    public java.util.List<User> findAll() {
        return jpaRepo.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * PAGINATION IMPLEMENTATION
     * 
     * INTERVIEW TIP: Explain Spring Data Page<T>
     * - Page<T> contains: content, totalElements, totalPages
     * - Pageable contains: page, size, sort
     * - We map Page<Entity> to Page<Domain>
     */
    @Override
    public org.springframework.data.domain.Page<User> findAll(
            org.springframework.data.domain.Pageable pageable) {
        return jpaRepo.findAll(pageable)
                .map(this::toDomain);
    }
    
    @Override
    public org.springframework.data.domain.Page<User> searchUsers(
            String searchTerm, 
            org.springframework.data.domain.Pageable pageable) {
        // Case-insensitive search in email, first name, last name
        return jpaRepo.findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                searchTerm, searchTerm, searchTerm, pageable)
                .map(this::toDomain);
    }
    
    @Override
    public org.springframework.data.domain.Page<User> findByRole(
            String role, 
            org.springframework.data.domain.Pageable pageable) {
        // Find users where roles collection contains the specified role
        return jpaRepo.findByRolesContaining(role, pageable)
                .map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity persisted = jpaRepo.save(entity);
        return toDomain(persisted);
    }
    
    /**
     * Map JPA entity to domain model (Phase 3 - complete mapping)
     * 
     * INTERVIEW: "Repository adapts between persistence and domain layers"
     * "This is the Adapter Pattern from Gang of Four design patterns"
     */
    private User toDomain(UserEntity entity) {
        Set<Role> roles = entity.getRoles().stream()
                .map(Role::fromString)
                .filter(role -> role != null)
                .collect(Collectors.toSet());
        
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFirstName(),
                entity.getLastName(),
                roles,
                entity.isActive(),
                entity.isEmailVerified(),
                entity.isAccountLocked(),
                entity.getLockedUntil(),
                entity.getFailedLoginAttempts(),
                entity.getLastLoginAt(),
                entity.getEmailVerificationToken(),
                entity.getEmailVerificationSentAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
    
    /**
     * Map domain model to JPA entity (Phase 3 - complete mapping)
     * 
     * INTERVIEW: "Domain → Entity mapping for persistence"
     * "Entity has setters, Domain is immutable - different concerns"
     */
    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoleNames()
        );
        
        // Set Phase 3 fields
        entity.setActive(user.isActive());
        entity.setEmailVerified(user.isEmailVerified());
        entity.setAccountLocked(user.isAccountLocked());
        entity.setLockedUntil(user.getLockedUntil());
        entity.setFailedLoginAttempts(user.getFailedLoginAttempts());
        entity.setLastLoginAt(user.getLastLoginAt());
        entity.setEmailVerificationToken(user.getEmailVerificationToken());
        entity.setEmailVerificationSentAt(user.getEmailVerificationSentAt());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        
        return entity;
    }
}