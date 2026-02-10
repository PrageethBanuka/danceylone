package com.danceylone.backend.shared.application;

import com.danceylone.backend.shared.domain.AuditAction;
import com.danceylone.backend.shared.domain.AuditLog;
import com.danceylone.backend.shared.domain.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Application service for audit logging.
 * 
 * Interview Tip: This is Application Service from DDD:
 * - Orchestrates business operations
 * - Coordinates between domain and infrastructure
 * - Doesn't contain business logic (that's in domain models)
 * - Handles cross-cutting concerns like logging and error handling
 */
@Service
public class AuditService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    
    private final AuditLogRepository auditLogRepository;
    
    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    
    /**
     * Log a user-related action.
     * 
     * Interview Tip: Audit logging should NEVER throw exceptions that break the main flow.
     * If audit fails, log the error but continue (resilience pattern).
     * 
     * @param userId The user being affected
     * @param performedBy The user performing the action
     * @param action The action being performed
     * @param details Additional context (JSON format recommended)
     * @param ipAddress User's IP address
     * @param userAgent User's browser/client
     */
    public void logUserAction(
            UUID userId,
            UUID performedBy,
            AuditAction action,
            String details,
            String ipAddress,
            String userAgent
    ) {
        try {
            AuditLog auditLog = AuditLog.forUser(
                    userId,
                    performedBy,
                    action,
                    details,
                    ipAddress,
                    userAgent
            );
            
            auditLogRepository.save(auditLog);
            
            logger.info("Audit log created: action={}, userId={}, performedBy={}", 
                       action, userId, performedBy);
            
        } catch (Exception e) {
            // Interview Tip: Never let audit failure break the main operation
            logger.error("Failed to save audit log: action={}, userId={}, error={}", 
                        action, userId, e.getMessage(), e);
        }
    }
    
    /**
     * Log a general action (not specific to users).
     * 
     * @param performedBy The user performing the action
     * @param action The action being performed
     * @param entityType Type of entity (e.g., "ORDER", "PRODUCT")
     * @param entityId ID of the entity
     * @param details Additional context
     * @param ipAddress User's IP address
     * @param userAgent User's browser/client
     */
    public void logAction(
            UUID performedBy,
            AuditAction action,
            String entityType,
            UUID entityId,
            String details,
            String ipAddress,
            String userAgent
    ) {
        try {
            AuditLog auditLog = new AuditLog(
                    UUID.randomUUID(),
                    null, // No specific user affected
                    performedBy,
                    action.name(),
                    entityType,
                    entityId,
                    details,
                    ipAddress,
                    userAgent,
                    LocalDateTime.now()
            );
            
            auditLogRepository.save(auditLog);
            
            logger.info("Audit log created: action={}, entityType={}, entityId={}, performedBy={}", 
                       action, entityType, entityId, performedBy);
            
        } catch (Exception e) {
            logger.error("Failed to save audit log: action={}, entityType={}, error={}", 
                        action, entityType, e.getMessage(), e);
        }
    }
    
    /**
     * Get audit logs for a specific user (paginated).
     * Used in user profile to show "Recent Activity".
     */
    public Page<AuditLog> getUserAuditLogs(UUID userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable);
    }
    
    /**
     * Get audit logs performed by a specific admin (paginated).
     * Used for admin accountability - "what did this admin do?"
     */
    public Page<AuditLog> getAdminAuditLogs(UUID adminId, Pageable pageable) {
        return auditLogRepository.findByPerformedBy(adminId, pageable);
    }
    
    /**
     * Search audit logs with filters (admin audit viewer).
     * 
     * Interview Tip: This is essential for compliance:
     * - SOC2 requires audit trail visibility
     * - GDPR requires tracking data access
     * - Security investigations need quick filtering
     */
    public Page<AuditLog> searchAuditLogs(
            UUID userId,
            UUID performedBy,
            String action,
            String entityType,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    ) {
        return auditLogRepository.searchAuditLogs(
                userId, performedBy, action, entityType, fromDate, toDate, pageable
        );
    }
    
    /**
     * Check if a user has had too many failed login attempts recently.
     * Used for brute force attack detection.
     * 
     * @param userId The user to check
     * @param windowMinutes How far back to look (e.g., 15 minutes)
     * @return Number of failed attempts
     */
    public long getRecentFailedLoginAttempts(UUID userId, int windowMinutes) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(windowMinutes);
        return auditLogRepository.countFailedLoginAttempts(userId, since);
    }
}
