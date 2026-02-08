package com.danceylone.backend.order.infrastructure;

import com.danceylone.backend.order.domain.Order;
import com.danceylone.backend.order.domain.OrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper to convert between Domain and JPA entities
 */
@Component
public class OrderMapper {

    public Order toDomain(OrderEntity entity) {
        return new Order(
            entity.getId(),
            entity.getUserId(),
            entity.getOrderNumber(),
            entity.getShippingAddress(),
            entity.getItems().stream()
                .map(this::toOrderItemDomain)
                .collect(Collectors.toList()),
            entity.getStatus(),
            entity.getCreatedAt()
        );
    }

    public OrderEntity toEntity(Order domain) {
        OrderEntity entity = new OrderEntity(
            domain.getId(),
            domain.getUserId(),
            domain.getOrderNumber(),
            domain.getShippingAddress(),
            domain.getStatus(),
            domain.getCreatedAt()
        );
        
        domain.getItems().forEach(item -> {
            OrderItemEntity itemEntity = toOrderItemEntity(item);
            entity.addItem(itemEntity);
        });
        
        return entity;
    }

    private OrderItem toOrderItemDomain(OrderItemEntity entity) {
        return new OrderItem(
            entity.getId(),
            entity.getProductId(),
            entity.getProductName(),
            entity.getUnitPrice(),
            entity.getQuantity()
        );
    }

    private OrderItemEntity toOrderItemEntity(OrderItem domain) {
        return new OrderItemEntity(
            domain.getId(),
            domain.getProductId(),
            domain.getProductName(),
            domain.getUnitPrice(),
            domain.getQuantity()
        );
    }
}
