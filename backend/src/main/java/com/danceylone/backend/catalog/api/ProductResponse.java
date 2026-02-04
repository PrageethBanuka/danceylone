package com.danceylone.backend.catalog.api;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Product Response DTO - What we send to frontend
 * 
 * WHY NOT send Domain object directly?
 * 1. Security: Domain might have internal fields we shouldn't expose
 * 2. API Stability: Can change domain without breaking API
 * 3. Flexibility: Different endpoints can send different views of same data
 * 
 * INTERNSHIP TIP: This is the "DTO Pattern" - very common in REST APIs
 */
public class ProductResponse {
    
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private Integer stockQuantity;
    private boolean available;  // Note: we expose isAvailable() not active flag directly

    // Default constructor for JSON deserialization
    public ProductResponse() {}

    public ProductResponse(UUID id, String name, String description, BigDecimal price,
                           String category, String imageUrl, Integer stockQuantity, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
        this.available = available;
    }

    // Getters and setters
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

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
