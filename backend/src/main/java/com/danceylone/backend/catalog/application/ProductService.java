package com.danceylone.backend.catalog.application;

import com.danceylone.backend.catalog.domain.Product;
import com.danceylone.backend.catalog.domain.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Product Service - Application Layer
 * 
 * RESPONSIBILITY: Orchestrate business use cases
 * 
 * DOES NOT:
 * - Handle HTTP requests (that's Controller's job)
 * - Execute SQL (that's Repository's job)
 * - Contain business rules (that's Domain's job)
 * 
 * DOES:
 * - Coordinate operations
 * - Manage transactions
 * - Provide clean API for controllers
 * 
 * INTERNSHIP TIP: Service layer is where you'd add:
 * - Permission checks
 * - Audit logging
 * - Event publishing
 * - Cross-entity operations
 */
@Service
@Transactional(readOnly = true)  // Default: read-only transactions (optimization)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Get all active products
     * USE CASE: Display product catalog on frontend
     */
    public List<Product> getAllProducts() {
        return productRepository.findAllActive();
    }

    /**
     * Get product by ID
     * USE CASE: Show product details page
     */
    public Optional<Product> getProductById(UUID id) {
        return productRepository.findById(id)
                .filter(Product::isActive);  // Only return if active
    }

    /**
     * Search products by name
     * USE CASE: Search bar functionality
     */
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllProducts();  // Empty search = show all
        }
        return productRepository.findByNameContaining(keyword);
    }

    /**
     * Get products by category
     * USE CASE: Category filter dropdown
     */
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    /**
     * Create new product
     * 
     * @Transactional (write mode) - changes committed to database
     * 
     * INTERNSHIP TIP: In real apps, this would be admin-only
     * You'd add @PreAuthorize("hasRole('ADMIN')") annotation
     */
    @Transactional  // Write transaction
    public Product createProduct(Product product) {
        // Domain entity already validates in constructor
        // No need for duplicate validation here
        return productRepository.save(product);
    }

    /**
     * Update existing product
     */
    @Transactional
    public Optional<Product> updateProduct(UUID id, Product updatedProduct) {
        if (!productRepository.existsById(id)) {
            return Optional.empty();  // Product not found
        }
        
        // Create new domain object with updated ID to ensure we're updating the right one
        Product productToSave = new Product(
                id,  // Keep original ID
                updatedProduct.getName(),
                updatedProduct.getDescription(),
                updatedProduct.getPrice(),
                updatedProduct.getCategory(),
                updatedProduct.getImageUrl(),
                updatedProduct.getStockQuantity(),
                updatedProduct.isActive()
        );
        
        return Optional.of(productRepository.save(productToSave));
    }

    /**
     * Delete product (soft delete - mark as inactive)
     * 
     * WHY SOFT DELETE:
     * - Preserve order history (customer bought "Product X" should still show)
     * - Data recovery if mistake
     * - Business analytics need historical data
     * 
     * INTERNSHIP TIP: Hard deletes are rare in production systems
     */
    @Transactional
    public boolean deactivateProduct(UUID id) {
        Optional<Product> existing = productRepository.findById(id);
        if (existing.isEmpty()) {
            return false;
        }
        
        Product product = existing.get();
        Product deactivated = new Product(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getImageUrl(),
                product.getStockQuantity(),
                false  // Set active to false
        );
        
        productRepository.save(deactivated);
        return true;
    }

    /**
     * Hard delete (for testing/cleanup)
     * RARELY used in production
     */
    @Transactional
    public void deleteProduct(UUID id) {
        productRepository.delete(id);
    }
}
