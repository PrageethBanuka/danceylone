package com.danceylone.backend.catalog.infrastructure;

import com.danceylone.backend.catalog.domain.Product;
import com.danceylone.backend.catalog.domain.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository Implementation - Adapter Pattern
 * 
 * ARCHITECTURE LESSON:
 * Domain depends on ProductRepository INTERFACE
 * This class IMPLEMENTS the interface using JPA
 * 
 * IF we switch to MongoDB tomorrow:
 * - Domain stays the same ✅
 * - Just create MongoProductRepositoryImpl ✅
 * 
 * INTERNSHIP TIP: This is "Dependency Inversion" from SOLID principles
 */
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaRepository;

    public ProductRepositoryImpl(JpaProductRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);  // Convert JPA → Domain
    }

    @Override
    public List<Product> findAllActive() {
        return jpaRepository.findByActiveTrue()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByCategory(String category) {
        return jpaRepository.findByCategoryAndActiveTrue(category)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByNameContaining(String keyword) {
        return jpaRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = toEntity(product);  // Convert Domain → JPA
        ProductEntity saved = jpaRepository.save(entity);
        return toDomain(saved);  // Convert back JPA → Domain
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    /**
     * MAPPER: JPA Entity → Domain Object
     * 
     * WHY NECESSARY:
     * - Domain objects are immutable (final fields)
     * - JPA entities have setters
     * - Keep database concerns OUT of domain
     */
    private Product toDomain(ProductEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getCategory(),
                entity.getImageUrl(),
                entity.getStockQuantity(),
                entity.isActive()
        );
    }

    /**
     * MAPPER: Domain Object → JPA Entity
     */
    private ProductEntity toEntity(Product product) {
        return new ProductEntity(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getImageUrl(),
                product.getStockQuantity(),
                product.isActive()
        );
    }
}
