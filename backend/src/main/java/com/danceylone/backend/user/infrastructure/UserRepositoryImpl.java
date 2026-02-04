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
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity persisted = jpaRepo.save(entity);
        return toDomain(persisted);
    }
    
    /**
     * Map JPA entity to domain model
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
                roles
        );
    }
    
    /**
     * Map domain model to JPA entity
     */
    private UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoleNames()
        );
    }
}