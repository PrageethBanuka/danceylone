package com.danceylone.backend.cart.api;

import com.danceylone.backend.cart.application.CartService;
import com.danceylone.backend.cart.domain.Cart;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Shopping cart management")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Get current user's cart")
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        UUID userId = getUserId(authentication);
        Cart cart = cartService.getCart(userId);
        return ResponseEntity.ok(toResponse(cart));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<CartResponse> addItem(
            @RequestBody AddToCartRequest request,
            Authentication authentication) {
        UUID userId = getUserId(authentication);
        Cart cart = cartService.addItem(userId, request.productId, request.quantity);
        return ResponseEntity.ok(toResponse(cart));
    }

    @PutMapping("/items/{productId}")
    @Operation(summary = "Update item quantity")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable UUID productId,
            @RequestBody UpdateCartItemRequest request,
            Authentication authentication) {
        UUID userId = getUserId(authentication);
        Cart cart = cartService.updateItemQuantity(userId, productId, request.quantity);
        return ResponseEntity.ok(toResponse(cart));
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable UUID productId,
            Authentication authentication) {
        UUID userId = getUserId(authentication);
        Cart cart = cartService.removeItem(userId, productId);
        return ResponseEntity.ok(toResponse(cart));
    }

    @DeleteMapping
    @Operation(summary = "Clear cart")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        UUID userId = getUserId(authentication);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    private UUID getUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("User is not authenticated");
        }
        
        String userId = (String) authentication.getPrincipal();
        return UUID.fromString(userId);
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
            .map(item -> new CartItemResponse(
                item.getProductId(),
                item.getProductName(),
                item.getPrice(),
                item.getQuantity(),
                item.getSubtotal()
            ))
            .collect(Collectors.toList());

        return new CartResponse(
            cart.getId(),
            items,
            cart.getTotalItemCount(),
            cart.calculateTotal()
        );
    }

    // DTOs
    record AddToCartRequest(UUID productId, int quantity) {}
    record UpdateCartItemRequest(int quantity) {}
    
    record CartItemResponse(
        UUID productId,
        String productName,
        BigDecimal price,
        int quantity,
        BigDecimal subtotal
    ) {}

    record CartResponse(
        UUID cartId,
        List<CartItemResponse> items,
        int totalItems,
        BigDecimal totalAmount
    ) {}
}
