package com.danceylone.backend.shared.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for audit_logs table.
 * 
 * Interview Tip: This is the infrastructure/persistence layer.
 * It's separate from the AuditLog domain model because:
 * 1. Domain models should be free of framework dependencies (clean architecture)
 * 2. JPA entities need to be mutable (setters) but domain should be immutable
 * 3. Allows switching persistence frameworks without changing domain
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_logs_user_id", columnList = "user_id"),
    @Index(name = "idx_audit_logs_performed_by", columnList = "performed_by"),
    @Index(name = "idx_audit_logs_created_at", columnList = "created_at DESC"),
    @Index(name = "idx_audit_logs_action", columnList = "action"),
    @Index(name = "idx_audit_logs_entity", columnList = "entity_type, entity_id")
})
public class AuditLogEntity {
    
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;
    
    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;
    
    @Column(name = "performed_by", columnDefinition = "UUID")
    private UUID performedBy;
    
    @Column(nullable = false, length = 100)
    private String action;
    
    @Column(name = "entity_type", length = 50)
    private String entityType;
    
    @Column(name = "entity_id", columnDefinition = "UUID")
    private UUID entityId;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // JPA requires a no-args constructor
    protected AuditLogEntity() {}
    
    public AuditLogEntity(
            UUID id,
            UUID userId,
            UUID performedBy,
            String action,
            String entityType,
            UUID entityId,
            String details,
            String ipAddress,
            String userAgent,
            LocalDateTime createdAt
    ) {
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
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public UUID getPerformedBy() {
        return performedBy;
    }
    
    public void setPerformedBy(UUID performedBy) {
        this.performedBy = performedBy;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public UUID getEntityId() {
        return entityId;
    }
    
    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
