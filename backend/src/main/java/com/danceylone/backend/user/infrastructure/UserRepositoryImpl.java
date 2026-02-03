package com.danceylone.backend.user.infrastructure;

import com.danceylone.backend.user.domain.UserRepository;
import com.danceylone.backend.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaRepo;

    public UserRepositoryImpl(JpaUserRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepo.findByEmail(email)
                .map(e -> new User(e.getId(), e.getEmail(), e.getPasswordHash(), 
                                  e.getFirstName(), e.getLastName()));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepo.findById(id)
                .map(e -> new User(e.getId(), e.getEmail(), e.getPasswordHash(),
                                  e.getFirstName(), e.getLastName()));
    }

    @Override
    public User save(User user) {
        UserEntity entity = new UserEntity(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getFirstName(),
                user.getLastName()
        );
        // Capture the persisted entity to include any DB-generated fields
        UserEntity persisted = jpaRepo.save(entity);
        
        // Map the persisted entity back to domain User to return DB state
        return new User(
                persisted.getId(),
                persisted.getEmail(),
                persisted.getPasswordHash(),
                persisted.getFirstName(),
                persisted.getLastName()
        );
    }
}