package com.danceylone.backend.cart.application;

import com.danceylone.backend.cart.domain.Cart;
import com.danceylone.backend.cart.infrastructure.CartEntity;
import com.danceylone.backend.cart.infrastructure.CartJpaRepository;
import com.danceylone.backend.cart.infrastructure.CartMapper;
import com.danceylone.backend.catalog.infrastructure.ProductEntity;
import com.danceylone.backend.catalog.infrastructure.JpaProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Cart Application Service
 * 
 * RESPONSIBILITIES:
 * - Orchestrate cart operations
 * - Validate product availability
 * - Manage transactions
 */
@Service
@Transactional
public class CartService {

    private final CartJpaRepository cartRepository;
    private final JpaProductRepository productRepository;
    private final CartMapper cartMapper;

    public CartService(CartJpaRepository cartRepository, 
                      JpaProductRepository productRepository,
                      CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartMapper = cartMapper;
    }

    /**
     * Get or create cart for user
     */
    public Cart getCart(UUID userId) {
        return cartRepository.findByUserId(userId)
            .map(cartMapper::toDomain)
            .orElseGet(() -> {
                Cart newCart = new Cart(UUID.randomUUID(), userId);
                return saveCart(newCart);
            });
    }

    /**
     * Add item to cart
     */
    public Cart addItem(UUID userId, UUID productId, int quantity) {
        // Get or create cart
        Cart cart = getCart(userId);

        // Fetch product
        ProductEntity product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        // Validate product availability
        if (!product.isActive()) {
            throw new IllegalStateException("Product is not available: " + product.getName());
        }

        if (product.getStockQuantity() < quantity) {
            throw new IllegalStateException("Insufficient stock for product: " + product.getName());
        }

        // Add to cart
        cart.addItem(productId, product.getName(), product.getPrice(), quantity);

        return saveCart(cart);
    }

    /**
     * Update item quantity
     */
    public Cart updateItemQuantity(UUID userId, UUID productId, int newQuantity) {
        Cart cart = getCart(userId);

        if (newQuantity > 0) {
            // Verify stock availability
            ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

            if (product.getStockQuantity() < newQuantity) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }
        }

        cart.updateItemQuantity(productId, newQuantity);

        return saveCart(cart);
    }

    /**
     * Remove item from cart
     */
    public Cart removeItem(UUID userId, UUID productId) {
        Cart cart = getCart(userId);
        cart.removeItem(productId);
        return saveCart(cart);
    }

    /**
     * Clear cart
     */
    public void clearCart(UUID userId) {
        cartRepository.findByUserId(userId)
            .ifPresent(cart -> {
                cart.clearItems();
                cartRepository.save(cart);
            });
    }

    private Cart saveCart(Cart cart) {
        CartEntity entity = cartMapper.toEntity(cart);
        CartEntity saved = cartRepository.save(entity);
        return cartMapper.toDomain(saved);
    }
}
