package com.danceylone.backend.cart.domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * CartItem - Value Object within Cart Aggregate
 * 
 * IMMUTABLE: Once created, fields cannot change
 * To update quantity, create a new CartItem
 */
public class CartItem {

    private final UUID id;
    private final UUID productId;
    private final String productName;
    private final BigDecimal price;
    private final int quantity;

    public CartItem(UUID id, UUID productId, String productName, BigDecimal price, int quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Create a new CartItem with updated quantity
     */
    public CartItem updateQuantity(int newQuantity) {
        return new CartItem(this.id, this.productId, this.productName, this.price, newQuantity);
    }

    /**
     * Calculate subtotal: price * quantity
     */
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getProductId() { return productId; }
    public String getProductName() { return productName; }
    public BigDecimal getPrice() { return price; }
    public int getQuantity() { return quantity; }
}
