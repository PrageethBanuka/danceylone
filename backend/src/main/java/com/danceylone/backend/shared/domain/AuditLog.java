package com.danceylone.backend.shared.domain;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Audit Log Domain Entity
 * 
 * PRODUCTION PATTERN: Audit Trail for Compliance
 * 
 * WHY AUDIT LOGGING?
 * 1. Security: Track who did what and when
 * 2. Compliance: SOC2, GDPR, HIPAA requirements
 * 3. Debugging: Trace issue origins
 * 4. Analytics: User behavior insights
 * 
 * INTERVIEW TALKING POINTS:
 * - "Every critical action generates an audit entry"
 * - "Immutable logs - never update or delete audit entries"
 * - "Retention policy: Keep for 7 years (compliance requirement)"
 * - "Separate table from operational data for performance"
 * 
 * REAL-WORLD EXAMPLES:
 * - WHO: Admin John (performedBy)
 * - DID WHAT: Deactivated user account (action: USER_DEACTIVATED)
 * - TO WHOM: User Jane (userId)
 * - WHEN: 2026-02-10 10:30 AM (createdAt)
 * - WHY: "Suspicious activity detected" (details)
 * - FROM WHERE: IP 192.168.1.100 (ipAddress)
 * 
 * MODULAR MONOLITH:
 * - Shared domain entity used across all modules
 * - User module, Order module, Product module all log here
 * - Single source of truth for audit trail
 */
public class AuditLog {
    
    private UUID id;
    private UUID userId;              // Who was affected
    private UUID performedBy;         // Who performed the action (admin/system)
    private String action;            // What happened (stored as String, use AuditAction.name())
    private String entityType;        // USER, ORDER, PRODUCT, etc.
    private UUID entityId;            // ID of the affected entity
    private String details;           // JSON with additional context
    private String ipAddress;         // Where it happened (IPv4/IPv6)
    private String userAgent;         // Browser/device information
    private LocalDateTime createdAt;  // When it happened
    
    /**
     * Constructor for creating new audit logs
     * IMMUTABLE: Once created, audit logs never change
     */
    public AuditLog(
            UUID id,
            UUID userId,
            UUID performedBy,
            String action,
            String entityType,
            UUID entityId,
            String details,
            String ipAddress,
            String userAgent,
            LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.performedBy = performedBy;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
    }
    
    /**
     * Factory method for user-related audit logs
     * INTERVIEW: "Factory methods make domain logic explicit"
     */
    public static AuditLog forUser(
            UUID userId,
            UUID performedBy,
            AuditAction action,
            String details,
            String ipAddress,
            String userAgent) {
        return new AuditLog(
            UUID.randomUUID(),
            userId,
            performedBy,
            action.name(), // Convert enum to String
            "USER",
            userId,
            details,
            ipAddress,
            userAgent,
            LocalDateTime.now()
        );
    }
    
    // Getters only - no setters (immutable)
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getPerformedBy() { return performedBy; }
    public String getAction() { return action; }
    public String getEntityType() { return entityType; }
    public UUID getEntityId() { return entityId; }
    public String getDetails() { return details; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    @Override
    public String toString() {
        return String.format("AuditLog{action=%s, userId=%s, performedBy=%s, at=%s}", 
            action, userId, performedBy, createdAt);
    }
}
