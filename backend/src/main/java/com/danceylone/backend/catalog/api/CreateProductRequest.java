package com.danceylone.backend.catalog.api;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Create Product Request DTO - What frontend sends us
 * 
 * VALIDATION ANNOTATIONS:
 * - @NotBlank: can't be null or empty string
 * - @NotNull: can't be null
 * - @DecimalMin: must be >= value
 * - @Min: integer must be >= value
 * 
 * SPRING MAGIC: Add @Valid to controller parameter
 * → Spring automatically validates & returns 400 Bad Request if invalid
 * 
 * INTERNSHIP TIP: This is "Bean Validation" (JSR-303)
 * Used by almost every Java web framework
 */
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    @NotBlank(message = "Category is required")
    private String category;

    private String imageUrl;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    // Default constructor
    public CreateProductRequest() {}

    public CreateProductRequest(String name, String description, BigDecimal price,
                                String category, String imageUrl, Integer stockQuantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
    }

    // Getters and setters
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
}
