package com.danceylone.backend.shared.api.dto;

import org.springframework.data.domain.Sort;

/**
 * Page Request DTO
 * 
 * PRODUCTION PATTERN: Pagination & Sorting
 * 
 * WHY PAGINATION?
 * - Performance: Don't load 10,000 users at once
 * - User Experience: Load data in chunks
 * - Network: Reduce payload size
 * - Database: Reduce query load
 * 
 * INTERVIEW TIP: Always mention pagination in production apps
 * "For scalability, I implemented cursor-based/offset pagination"
 * 
 * MODULAR MONOLITH:
 * - Reusable DTO across all modules
 * - Standardized pagination across catalog, users, orders
 */
public record PageRequest(
    int page,           // 0-indexed page number
    int size,           // Items per page (usually 10, 20, 50)
    String sortBy,      // Field to sort by (email, firstName, createdAt)
    String direction    // ASC or DESC
) {
    /**
     * Default values for pagination
     * PRODUCTION: Always have sensible defaults
     */
    public PageRequest {
        if (page < 0) page = 0;
        if (size < 1 || size > 100) size = 20; // Max 100 per page
        if (sortBy == null || sortBy.isBlank()) sortBy = "email";
        if (direction == null || direction.isBlank()) direction = "ASC";
    }
    
    /**
     * Convert to Spring Data Sort object
     * ADAPTER PATTERN: Convert our DTO to framework object
     */
    public Sort toSort() {
        Sort.Direction dir = "DESC".equalsIgnoreCase(direction) 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        return Sort.by(dir, sortBy);
    }
    
    /**
     * Convert to Spring Data PageRequest
     */
    public org.springframework.data.domain.PageRequest toPageRequest() {
        return org.springframework.data.domain.PageRequest.of(page, size, toSort());
    }
}
