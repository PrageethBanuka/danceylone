package com.danceylone.backend.shared.api.dto;

import java.util.List;

/**
 * Paginated Response DTO
 * 
 * PRODUCTION PATTERN: Consistent API Response Format
 * 
 * WHY THIS STRUCTURE?
 * - Frontend needs metadata (total pages, current page)
 * - Better UX: Show "Page 1 of 10"
 * - Enable infinite scroll or pagination controls
 * 
 * MODULAR MONOLITH:
 * - Generic type T = works for Users, Products, Orders
 * - Shared across all modules
 * - Single source of truth for pagination response
 * 
 * INTERVIEW TIP: Explain pagination metadata
 * "totalElements helps calculate total pages"
 * "hasNext/hasPrevious for navigation controls"
 */
public record PageResponse<T>(
    List<T> content,        // The actual data
    int page,               // Current page (0-indexed)
    int size,               // Items per page
    long totalElements,     // Total items across all pages
    int totalPages,         // Total number of pages
    boolean hasNext,        // Can we go forward?
    boolean hasPrevious,    // Can we go back?
    String sortBy,          // What field we sorted by
    String direction        // ASC or DESC
) {
    /**
     * Factory method from Spring Data Page
     * FACTORY PATTERN: Convenient creation
     */
    public static <T> PageResponse<T> from(
            org.springframework.data.domain.Page<T> page,
            String sortBy,
            String direction) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext(),
            page.hasPrevious(),
            sortBy,
            direction
        );
    }
}
