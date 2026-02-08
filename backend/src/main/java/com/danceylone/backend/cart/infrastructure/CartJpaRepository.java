package com.danceylone.backend.cart.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartJpaRepository extends JpaRepository<CartEntity, UUID> {
    
    Optional<CartEntity> findByUserId(UUID userId);
    
    void deleteByUserId(UUID userId);
}
