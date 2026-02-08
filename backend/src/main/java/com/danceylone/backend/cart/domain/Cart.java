package com.danceylone.backend.cart.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cart Domain Entity - Shopping Cart Aggregate Root
 * 
 * AGGREGATE ROOT: Cart owns CartItems - they cannot exist without a Cart
 * BUSINESS RULES:
 * - Each user has exactly one cart
 * - Cart items reference products
 * - Cart calculates total automatically
 */
public class Cart {

    private final UUID id;
    private final UUID userId;
    private final List<CartItem> items;

    public Cart(UUID id, UUID userId) {
        this(id, userId, new ArrayList<>());
    }

    public Cart(UUID id, UUID userId, List<CartItem> items) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        this.id = id;
        this.userId = userId;
        this.items = new ArrayList<>(items != null ? items : Collections.emptyList());
    }

    /**
     * Add item to cart or update quantity if already exists
     */
    public void addItem(UUID productId, String productName, BigDecimal price, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Optional<CartItem> existingItem = findItemByProductId(productId);
        
        if (existingItem.isPresent()) {
            // Update quantity of existing item
            CartItem item = existingItem.get();
            items.remove(item);
            items.add(item.updateQuantity(item.getQuantity() + quantity));
        } else {
            // Add new item
            items.add(new CartItem(UUID.randomUUID(), productId, productName, price, quantity));
        }
    }

    /**
     * Update item quantity (or remove if quantity is 0)
     */
    public void updateItemQuantity(UUID productId, int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        CartItem item = findItemByProductId(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not in cart: " + productId));

        items.remove(item);
        
        if (newQuantity > 0) {
            items.add(item.updateQuantity(newQuantity));
        }
        // If newQuantity is 0, item is removed (not re-added)
    }

    /**
     * Remove item from cart
     */
    public void removeItem(UUID productId) {
        CartItem item = findItemByProductId(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not in cart: " + productId));
        items.remove(item);
    }

    /**
     * Clear all items from cart
     */
    public void clear() {
        items.clear();
    }

    /**
     * Calculate total price of all items in cart
     */
    public BigDecimal calculateTotal() {
        return items.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get total item count (sum of all quantities)
     */
    public int getTotalItemCount() {
        return items.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }

    /**
     * Check if cart is empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    private Optional<CartItem> findItemByProductId(UUID productId) {
        return items.stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst();
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public List<CartItem> getItems() { return Collections.unmodifiableList(items); }
}
