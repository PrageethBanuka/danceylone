package com.danceylone.backend.order.application;

import com.danceylone.backend.cart.application.CartService;
import com.danceylone.backend.cart.domain.Cart;
import com.danceylone.backend.cart.domain.CartItem;
import com.danceylone.backend.catalog.infrastructure.ProductEntity;
import com.danceylone.backend.catalog.infrastructure.JpaProductRepository;
import com.danceylone.backend.order.domain.Order;
import com.danceylone.backend.order.domain.OrderItem;
import com.danceylone.backend.order.domain.OrderStatus;
import com.danceylone.backend.order.infrastructure.OrderEntity;
import com.danceylone.backend.order.infrastructure.OrderJpaRepository;
import com.danceylone.backend.order.infrastructure.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Order Application Service
 * 
 * RESPONSIBILITIES:
 * - Create orders from cart
 * - Manage order lifecycle
 * - Update inventory on order
 */
@Service
@Transactional
public class OrderService {

    private final OrderJpaRepository orderRepository;
    private final JpaProductRepository productRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;

    public OrderService(OrderJpaRepository orderRepository,
                       JpaProductRepository productRepository,
                       CartService cartService,
                       OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.orderMapper = orderMapper;
    }

    /**
     * Create order from cart
     */
    public Order createOrder(UUID userId, String shippingAddress) {
        // Get user's cart
        Cart cart = cartService.getCart(userId);

        if (cart.isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }

        // Verify stock availability for all items
        for (CartItem cartItem : cart.getItems()) {
            ProductEntity product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new IllegalStateException("Product not found: " + cartItem.getProductId()));

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }
        }

        // Create order items
        List<OrderItem> orderItems = cart.getItems().stream()
            .map(cartItem -> new OrderItem(
                UUID.randomUUID(),
                cartItem.getProductId(),
                cartItem.getProductName(),
                cartItem.getPrice(),
                cartItem.getQuantity()
            ))
            .collect(Collectors.toList());

        // Generate order number
        String orderNumber = generateOrderNumber();

        // Create order
        Order order = new Order(
            UUID.randomUUID(),
            userId,
            orderNumber,
            shippingAddress,
            orderItems
        );

        // Save order
        OrderEntity entity = orderMapper.toEntity(order);
        OrderEntity saved = orderRepository.save(entity);

        // Update inventory
        updateInventory(cart);

        // Clear cart
        cartService.clearCart(userId);

        return orderMapper.toDomain(saved);
    }

    /**
     * Get user's orders
     */
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(orderMapper::toDomain)
            .collect(Collectors.toList());
    }

    /**
     * Get all orders (admin only)
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(orderMapper::toDomain)
            .collect(Collectors.toList());
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public Order getOrder(UUID orderId) {
        OrderEntity entity = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        return orderMapper.toDomain(entity);
    }

    /**
     * Cancel order
     */
    public Order cancelOrder(UUID orderId) {
        OrderEntity entity = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        Order order = orderMapper.toDomain(entity);

        if (!order.isCancellable()) {
            throw new IllegalStateException("Order cannot be cancelled");
        }

        order.cancel();

        // Restore inventory
        restoreInventory(order);

        entity.setStatus(order.getStatus());
        OrderEntity saved = orderRepository.save(entity);

        return orderMapper.toDomain(saved);
    }

    /**
     * Update order status
     */
    public Order updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        OrderEntity entity = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        Order order = orderMapper.toDomain(entity);

        // Apply status transition
        switch (newStatus) {
            case CONFIRMED:
                order.confirm();
                break;
            case PROCESSING:
                order.process();
                break;
            case SHIPPED:
                order.ship();
                break;
            case DELIVERED:
                order.deliver();
                break;
            case CANCELLED:
                order.cancel();
                restoreInventory(order);
                break;
            default:
                throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        entity.setStatus(order.getStatus());
        OrderEntity saved = orderRepository.save(entity);

        return orderMapper.toDomain(saved);
    }

    private void updateInventory(Cart cart) {
        cart.getItems().forEach(cartItem -> {
            ProductEntity product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new IllegalStateException("Product not found: " + cartItem.getProductId()));

            int newStock = product.getStockQuantity() - cartItem.getQuantity();
            product.setStockQuantity(newStock);
            productRepository.save(product);
        });
    }

    private void restoreInventory(Order order) {
        order.getItems().forEach(orderItem -> {
            productRepository.findById(orderItem.getProductId())
                .ifPresent(product -> {
                    int newStock = product.getStockQuantity() + orderItem.getQuantity();
                    product.setStockQuantity(newStock);
                    productRepository.save(product);
                });
        });
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD-" + timestamp;
    }
}
