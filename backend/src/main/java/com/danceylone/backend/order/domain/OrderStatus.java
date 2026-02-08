package com.danceylone.backend.order.domain;

/**
 * Order Status Enum - Represents order lifecycle
 */
public enum OrderStatus {
    PENDING,      // Order created, awaiting payment
    CONFIRMED,    // Payment confirmed
    PROCESSING,   // Being prepared/packed
    SHIPPED,      // In transit
    DELIVERED,    // Completed
    CANCELLED     // Cancelled by user or system
}
