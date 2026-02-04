package com.danceylone.backend.catalog.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Interface - The "Port" in Hexagonal Architecture
 * 
 * WHY AN INTERFACE?
 * 1. Domain doesn't care HOW data is stored (database, file, memory)
 * 2. Easy to swap implementations (JPA → MongoDB)
 * 3. Easy to mock for testing (crucial for TDD)
 * 
 * INTERNSHIP GOLD: This is "Dependency Inversion" from SOLID principles
 * Domain depends on abstraction, not concrete database code
 */
public interface ProductRepository {

    /**
     * Find product by unique ID
     * Returns Optional to handle "not found" elegantly (no null checks!)
     */
    Optional<Product> findById(UUID id);

    /**
     * Find all active products - useful for catalog display
     * WHY separate from findAll()? Business rule: only show active products to customers
     */
    List<Product> findAllActive();

    /**
     * Find products by category - enables filtering
     * LEARNING: Real apps have 10+ query methods (findByPriceRange, findByNameContaining, etc)
     */
    List<Product> findByCategory(String category);

    /**
     * Search products by name - for search functionality
     * "Containing" = partial match (like SQL LIKE '%keyword%')
     */
    List<Product> findByNameContaining(String keyword);

    /**
     * Save new or update existing product
     * PATTERN: CUD operations (Create, Update, Delete) return the saved entity
     * WHY? Database might modify it (auto-generated IDs, timestamps)
     */
    Product save(Product product);

    /**
     * Delete product - admin functionality
     * DEBATE: Should we actually delete or just mark inactive?
     * Many companies use "soft delete" (active=false) to preserve history
     */
    void delete(UUID id);

    /**
     * Check if product exists - useful before operations
     * OPTIMIZATION: Cheaper than fetching full object just to check existence
     */
    boolean existsById(UUID id);
}
