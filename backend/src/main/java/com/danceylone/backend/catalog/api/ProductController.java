package com.danceylone.backend.catalog.api;

import com.danceylone.backend.catalog.application.ProductService;
import com.danceylone.backend.catalog.domain.Product;
import com.danceylone.backend.shared.application.AuditService;
import com.danceylone.backend.shared.domain.AuditAction;
import com.danceylone.backend.user.domain.User;
import com.danceylone.backend.user.domain.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Product REST Controller - HTTP API Layer
 * 
 * RESPONSIBILITY: Handle HTTP requests/responses
 * 
 * REST PRINCIPLES:
 * - GET: Retrieve data (no side effects)
 * - POST: Create new resource
 * - PUT: Update entire resource
 * - PATCH: Update part of resource
 * - DELETE: Remove resource
 * 
 * HTTP STATUS CODES:
 * - 200 OK: Success
 * - 201 Created: Resource created
 * - 400 Bad Request: Validation failed
 * - 404 Not Found: Resource doesn't exist
 * - 500 Internal Server Error: Something broke
 * 
 * INTERNSHIP TIP: RESTful design is CRITICAL for interviews
 * Practice explaining why each endpoint uses its HTTP method
 */
@RestController
@RequestMapping("/api/products")  // Base path for all endpoints
@Tag(name = "Products", description = "Product catalog management endpoints")
public class ProductController {

    private final ProductService productService;
    private final AuditService auditService;
    private final UserRepository userRepository;

    public ProductController(ProductService productService, AuditService auditService, UserRepository userRepository) {
        this.productService = productService;
        this.auditService = auditService;
        this.userRepository = userRepository;
    }

    /**
     * GET /api/products
     * Get all active products
     * 
     * Optional query params:
     * - category: filter by category
     * - search: search by name
     */
    @Operation(
            summary = "Get all products",
            description = "Retrieves all active products with optional filtering by category or search term"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @Parameter(description = "Filter by product category") @RequestParam(required = false) String category,
            @Parameter(description = "Search products by name") @RequestParam(required = false) String search
    ) {
        List<Product> products;

        if (category != null && !category.isBlank()) {
            products = productService.getProductsByCategory(category);
        } else if (search != null && !search.isBlank()) {
            products = productService.searchProducts(search);
        } else {
            products = productService.getAllProducts();
        }

        // Convert Domain → DTO
        List<ProductResponse> response = products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);  // 200 OK
    }

    /**
     * GET /api/products/{id}
     * Get single product by ID
     * 
     * INTERNSHIP TIP: Using ResponseEntity lets you control:
     * - HTTP status code
     * - Response headers
     * - Response body
     */
    @Operation(
            summary = "Get product by ID",
            description = "Retrieves a single product by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Product UUID") @PathVariable UUID id) {
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(toResponse(product)))  // 200 OK
                .orElse(ResponseEntity.notFound().build());  // 404 Not Found
    }

    /**
     * POST /api/products
     * Create new product
     * 
     * @Valid triggers automatic validation of CreateProductRequest
     * If validation fails → Spring returns 400 Bad Request automatically
     */
    @Operation(
            summary = "Create new product",
            description = "Creates a new product in the catalog. Requires admin role.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - admin role required"
            )
    })
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request,
            HttpServletRequest httpRequest
    ) {
        // Convert DTO → Domain
        Product product = new Product(
                UUID.randomUUID(),  // Generate new ID
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getCategory(),
                request.getImageUrl(),
                request.getStockQuantity(),
                true  // New products are active by default
        );

        Product created = productService.createProduct(product);
        
        // Audit logging
        try {
            UUID performedBy = getCurrentUserId();
            auditService.logAction(
                performedBy,
                AuditAction.PRODUCT_CREATED,
                "PRODUCT",
                created.getId(),
                String.format("Product created: %s (Category: %s, Price: $%.2f)", 
                             created.getName(), created.getCategory(), created.getPrice()),
                getClientIp(httpRequest),
                getUserAgent(httpRequest)
            );
        } catch (Exception e) {
            // Audit logging should never break the main flow
        }
        
        return ResponseEntity
                .status(HttpStatus.CREATED)  // 201 Created
                .body(toResponse(created));
    }

    /**
     * PUT /api/products/{id}
     * Update entire product
     * 
     * PUT = replace entire resource
     * PATCH = update some fields (we could add this later)
     */
    @Operation(
            summary = "Update product",
            description = "Updates an existing product. Requires admin role.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product UUID") @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request,
            HttpServletRequest httpRequest
    ) {
        // For update, we need to merge existing data with request data
        // In real app, you'd get existing product first, then apply changes
        
        return productService.getProductById(id)
                .map(existing -> {
                    // Apply updates (only non-null fields)
                    Product updated = new Product(
                            id,  // Keep same ID
                            request.getName() != null ? request.getName() : existing.getName(),
                            request.getDescription() != null ? request.getDescription() : existing.getDescription(),
                            request.getPrice() != null ? request.getPrice() : existing.getPrice(),
                            request.getCategory() != null ? request.getCategory() : existing.getCategory(),
                            request.getImageUrl() != null ? request.getImageUrl() : existing.getImageUrl(),
                            request.getStockQuantity() != null ? request.getStockQuantity() : existing.getStockQuantity(),
                            request.getActive() != null ? request.getActive() : existing.isActive()
                    );

                    Product saved = productService.updateProduct(id, updated)
                            .orElseThrow();  // Should never throw since we just checked existence

                    // Audit logging
                    try {
                        UUID performedBy = getCurrentUserId();
                        auditService.logAction(
                            performedBy,
                            AuditAction.PRODUCT_UPDATED,
                            "PRODUCT",
                            saved.getId(),
                            String.format("Product updated: %s (Category: %s, Price: $%.2f, Stock: %d)", 
                                         saved.getName(), saved.getCategory(), saved.getPrice(), saved.getStockQuantity()),
                            getClientIp(httpRequest),
                            getUserAgent(httpRequest)
                        );
                    } catch (Exception e) {
                        // Audit logging should never break the main flow
                    }

                    return ResponseEntity.ok(toResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/products/{id}
     * Deactivate product (soft delete)
     * 
     * RETURNS 204 No Content on success
     * This is REST convention for successful deletes
     */
    @Operation(
            summary = "Delete product",
            description = "Soft deletes a product by deactivating it. Requires admin role.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Product deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product UUID") @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        
        // Get product info before deletion for audit log
        Product product = productService.getProductById(id).orElse(null);
        
        boolean deleted = productService.deactivateProduct(id);
        
        if (deleted && product != null) {
            // Audit logging
            try {
                UUID performedBy = getCurrentUserId();
                auditService.logAction(
                    performedBy,
                    AuditAction.PRODUCT_DELETED,
                    "PRODUCT",
                    id,
                    String.format("Product deleted: %s (Category: %s)", 
                                 product.getName(), product.getCategory()),
                    getClientIp(httpRequest),
                    getUserAgent(httpRequest)
                );
            } catch (Exception e) {
                // Audit logging should never break the main flow
            }
            
            return ResponseEntity.noContent().build();  // 204 No Content
        } else {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }
    }
    
    /**
     * Get current authenticated user's ID
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user");
        }
        
        String email = authentication.getPrincipal().toString();
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new NoSuchElementException("Current user not found"));
    }

    /**
     * Extract client IP address from request
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Extract user agent from request
     */
    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    /**
     * MAPPER: Domain → DTO Response
     * 
     * Centralizes conversion logic
     * If response format changes, only modify this method
     */
    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getImageUrl(),
                product.getStockQuantity(),
                product.isAvailable()  // Use domain business logic
        );
    }
}
