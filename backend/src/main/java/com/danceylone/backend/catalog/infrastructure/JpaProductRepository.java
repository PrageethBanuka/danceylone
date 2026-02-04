package com.danceylone.backend.catalog.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository - Database Operations
 * 
 * MAGIC: Just declare methods, Spring generates SQL automatically!
 * 
 * NAMING CONVENTION MATTERS:
 * - findBy... = SELECT query
 * - deleteBy... = DELETE query
 * - existsBy... = COUNT query
 * 
 * INTERNSHIP TIP: This is why companies use Spring Data -
 * no boilerplate SQL code to maintain!
 */
@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, UUID> {

    /**
     * Spring Data automatically generates:
     * SELECT * FROM products WHERE active = true
     */
    List<ProductEntity> findByActiveTrue();

    /**
     * Generates: SELECT * FROM products WHERE category = ?
     */
    List<ProductEntity> findByCategory(String category);

    /**
     * Generates: SELECT * FROM products WHERE name LIKE %?%
     * "Containing" = case-insensitive partial match
     */
    List<ProductEntity> findByNameContainingIgnoreCase(String keyword);

    /**
     * Combines conditions with AND
     * Generates: SELECT * FROM products WHERE category = ? AND active = true
     */
    List<ProductEntity> findByCategoryAndActiveTrue(String category);
}
