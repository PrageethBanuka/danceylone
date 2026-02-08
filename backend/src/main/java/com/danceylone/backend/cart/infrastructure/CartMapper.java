package com.danceylone.backend.cart.infrastructure;

import com.danceylone.backend.cart.domain.Cart;
import com.danceylone.backend.cart.domain.CartItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper to convert between Domain and JPA entities
 */
@Component
public class CartMapper {

    public Cart toDomain(CartEntity entity) {
        return new Cart(
            entity.getId(),
            entity.getUserId(),
            entity.getItems().stream()
                .map(this::toCartItemDomain)
                .collect(Collectors.toList())
        );
    }

    public CartEntity toEntity(Cart domain) {
        CartEntity entity = new CartEntity(domain.getId(), domain.getUserId());
        
        domain.getItems().forEach(item -> {
            CartItemEntity itemEntity = toCartItemEntity(item);
            entity.addItem(itemEntity);
        });
        
        return entity;
    }

    private CartItem toCartItemDomain(CartItemEntity entity) {
        return new CartItem(
            entity.getId(),
            entity.getProductId(),
            entity.getProductName(),
            entity.getPrice(),
            entity.getQuantity()
        );
    }

    private CartItemEntity toCartItemEntity(CartItem domain) {
        return new CartItemEntity(
            domain.getId(),
            domain.getProductId(),
            domain.getProductName(),
            domain.getPrice(),
            domain.getQuantity()
        );
    }
}
