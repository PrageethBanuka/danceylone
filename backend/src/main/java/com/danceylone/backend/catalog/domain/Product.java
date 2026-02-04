package com.danceylone.backend.catalog.domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Product Domain Entity - Core Business Object
 * 
 * WHY THIS MATTERS:
 * - Domain entities represent real business concepts (not just database tables!)
 * - Immutable design (final fields) prevents bugs and makes code thread-safe
 * - Business logic lives HERE, not scattered in services/controllers
 * 
 * INTERNSHIP TIP: Companies love when you can explain "domain model" vs "data model"
 */
public class Product {

    private final UUID id;
    private final String name;
    private final String description;
    private final BigDecimal price;  // WHY BigDecimal? Never use double/float for money! (precision errors)
    private final String category;
    private final String imageUrl;
    private final Integer stockQuantity;
    private final boolean active;

    /**
     * Constructor enforces business rules at creation time
     * This is called "invariants" - conditions that must ALWAYS be true
     */
    public Product(UUID id, String name, String description, BigDecimal price, 
                   String category, String imageUrl, Integer stockQuantity, boolean active) {
        // Validation: Fail fast if business rules violated
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price must be non-negative");
        }
        
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
        this.active = active;
    }

    // Getters only - no setters! Immutability prevents unexpected state changes
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public Integer getStockQuantity() { return stockQuantity; }
    public boolean isActive() { return active; }

    /**
     * Business logic: Can this product be purchased?
     * WHY HERE: Domain logic belongs in domain objects, not in services
     * BENEFIT: Easy to test, reusable, self-documenting
     */
    public boolean isAvailable() {
        return active && stockQuantity != null && stockQuantity > 0;
    }

    /**
     * Business logic: Check if enough stock exists for order
     * LEARNING: Methods that answer questions = "Query methods"
     */
    public boolean hasStock(int requestedQuantity) {
        return isAvailable() && stockQuantity >= requestedQuantity;
    }
}
