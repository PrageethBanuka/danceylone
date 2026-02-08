package com.danceylone.backend.order.infrastructure;

import com.danceylone.backend.order.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
    
    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    Optional<OrderEntity> findByOrderNumber(String orderNumber);
    
    List<OrderEntity> findByStatus(OrderStatus status);
}
