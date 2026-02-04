package com.danceylone.backend.catalog.infrastructure;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * JPA Entity - Database Representation
 * 
 * WHY SEPARATE from Domain Entity?
 * - Domain = business rules (clean, framework-independent)
 * - JPA Entity = database mapping (full of @annotations)
 * 
 * BENEFIT: Can change database tech without touching business logic
 * INTERNSHIP TIP: This is the "Adapter" pattern from Clean Architecture
 */
@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)  // Longer text for descriptions
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)  // precision=10 digits, scale=2 decimals (e.g., 12345678.99)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(nullable = false)
    private boolean active = true;  // Default new products to active

    // JPA requires default constructor (even if private)
    protected ProductEntity() {}

    public ProductEntity(UUID id, String name, String description, BigDecimal price,
                         String category, String imageUrl, Integer stockQuantity, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
        this.active = active;
    }

    // Getters and setters - JPA needs setters (unlike domain entity)
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
