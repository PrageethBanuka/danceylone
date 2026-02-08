package com.danceylone.backend.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Order Domain Entity - Order Aggregate Root
 * 
 * LIFECYCLE: PENDING -> CONFIRMED -> PROCESSING -> SHIPPED -> DELIVERED
 *           (or) PENDING -> CANCELLED
 * 
 * BUSINESS RULES:
 * - Orders cannot be modified once confirmed
 * - Total is calculated from order items
 * - Each order has unique order number
 */
public class Order {

    private final UUID id;
    private final UUID userId;
    private final String orderNumber;
    private final List<OrderItem> items;
    private final String shippingAddress;
    private OrderStatus status;
    private final LocalDateTime createdAt;

    public Order(UUID id, UUID userId, String orderNumber, String shippingAddress, List<OrderItem> items) {
        this(id, userId, orderNumber, shippingAddress, items, OrderStatus.PENDING, LocalDateTime.now());
    }

    public Order(UUID id, UUID userId, String orderNumber, String shippingAddress, 
                 List<OrderItem> items, OrderStatus status, LocalDateTime createdAt) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (orderNumber == null || orderNumber.isBlank()) {
            throw new IllegalArgumentException("Order number cannot be empty");
        }
        if (shippingAddress == null || shippingAddress.isBlank()) {
            throw new IllegalArgumentException("Shipping address cannot be empty");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        this.id = id;
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.shippingAddress = shippingAddress;
        this.items = new ArrayList<>(items);
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * Confirm the order (move from PENDING to CONFIRMED)
     */
    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be confirmed");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    /**
     * Start processing the order
     */
    public void process() {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed orders can be processed");
        }
        this.status = OrderStatus.PROCESSING;
    }

    /**
     * Mark order as shipped
     */
    public void ship() {
        if (status != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Only processing orders can be shipped");
        }
        this.status = OrderStatus.SHIPPED;
    }

    /**
     * Mark order as delivered
     */
    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Only shipped orders can be delivered");
        }
        this.status = OrderStatus.DELIVERED;
    }

    /**
     * Cancel the order
     */
    public void cancel() {
        if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Delivered orders cannot be cancelled");
        }
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        this.status = OrderStatus.CANCELLED;
    }

    /**
     * Calculate total amount
     */
    public BigDecimal getTotalAmount() {
        return items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Check if order can be cancelled
     */
    public boolean isCancellable() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getOrderNumber() { return orderNumber; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public String getShippingAddress() { return shippingAddress; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
