package com.danceylone.backend.shared.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain repository interface for audit logs.
 * 
 * Interview Tip: This interface is in the domain layer, but implemented in infrastructure.
 * This is Dependency Inversion Principle from SOLID:
 * - High-level domain doesn't depend on low-level infrastructure
 * - Both depend on this abstraction
 * - Allows testing domain logic without database
 */
public interface AuditLogRepository {
    
    /**
     * Save an audit log entry.
     * Interview Tip: In production, this should NEVER fail silently.
     * Audit logs are critical for compliance and security investigations.
     */
    AuditLog save(AuditLog auditLog);
    
    /**
     * Find all audit logs for a specific user (paginated).
     */
    Page<AuditLog> findByUserId(UUID userId, Pageable pageable);
    
    /**
     * Find all audit logs performed by a specific admin/user (paginated).
     */
    Page<AuditLog> findByPerformedBy(UUID performedBy, Pageable pageable);
    
    /**
     * Find audit logs by action type after a specific date.
     * Used for security monitoring (e.g., failed login attempts).
     */
    List<AuditLog> findByActionAfter(String action, LocalDateTime after);
    
    /**
     * Search audit logs with multiple filters (admin audit viewer).
     */
    Page<AuditLog> searchAuditLogs(
        UUID userId,
        UUID performedBy,
        String action,
        String entityType,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        Pageable pageable
    );
    
    /**
     * Count failed login attempts for a user since a specific time.
     * Used for brute force attack detection.
     */
    long countFailedLoginAttempts(UUID userId, LocalDateTime since);
}
