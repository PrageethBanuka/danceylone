package com.danceylone.backend.order.api;

import com.danceylone.backend.order.application.OrderService;
import com.danceylone.backend.order.domain.Order;
import com.danceylone.backend.order.domain.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create order from cart")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        UUID userId = getUserId(authentication);
        Order order = orderService.createOrder(userId, request.shippingAddress);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(order));
    }

    @GetMapping
    @Operation(summary = "Get user's orders")
    public ResponseEntity<List<OrderResponse>> getUserOrders(Authentication authentication) {
        UUID userId = getUserId(authentication);
        List<Order> orders = orderService.getUserOrders(userId);
        List<OrderResponse> responses = orders.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all orders (admin only)")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderResponse> responses = orders.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        Order order = orderService.getOrder(orderId);
        return ResponseEntity.ok(toResponse(order));
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID orderId) {
        Order order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(toResponse(order));
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status (admin only)")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        Order order = orderService.updateOrderStatus(orderId, request.status);
        return ResponseEntity.ok(toResponse(order));
    }

    private UUID getUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("User is not authenticated");
        }
        
        String userId = (String) authentication.getPrincipal();
        return UUID.fromString(userId);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
            .map(item -> new OrderItemResponse(
                item.getProductId(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal()
            ))
            .collect(Collectors.toList());

        return new OrderResponse(
            order.getId(),
            order.getOrderNumber(),
            order.getStatus(),
            items,
            order.getTotalAmount(),
            order.getShippingAddress(),
            order.getCreatedAt()
        );
    }

    // DTOs
    record CreateOrderRequest(String shippingAddress) {}
    record UpdateOrderStatusRequest(OrderStatus status) {}
    
    record OrderItemResponse(
        UUID productId,
        String productName,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal subtotal
    ) {}

    record OrderResponse(
        UUID id,
        String orderNumber,
        OrderStatus status,
        List<OrderItemResponse> items,
        BigDecimal totalAmount,
        String shippingAddress,
        LocalDateTime createdAt
    ) {}
}
