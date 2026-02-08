package com.danceylone.backend.order.domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * OrderItem - Value Object within Order Aggregate
 * 
 * IMMUTABLE: Captures product snapshot at time of order
 * Price and product name are stored here (not just ID) because:
 * - Product prices may change over time
 * - We need historical record of what was ordered
 */
public class OrderItem {

    private final UUID id;
    private final UUID productId;
    private final String productName;
    private final BigDecimal unitPrice;
    private final int quantity;

    public OrderItem(UUID id, UUID productId, String productName, BigDecimal unitPrice, int quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price must be non-negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /**
     * Calculate subtotal: unitPrice * quantity
     */
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getProductId() { return productId; }
    public String getProductName() { return productName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
}
