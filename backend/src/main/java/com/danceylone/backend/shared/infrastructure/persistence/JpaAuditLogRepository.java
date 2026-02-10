package com.danceylone.backend.shared.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for audit logs.
 * 
 * Interview Tip: This interface shows Spring Data JPA's magic:
 * - We only define the interface, Spring generates the implementation
 * - Method names follow conventions (findBy*, deleteBy*, etc.)
 * - Complex queries use @Query with JPQL
 * - Returns Page<T> for pagination support
 */
@Repository
public interface JpaAuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
    
    /**
     * Find all audit logs for a specific user (paginated).
     * Useful for showing a user's activity history.
     */
    Page<AuditLogEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    /**
     * Find all audit logs performed by a specific admin/user (paginated).
     * Useful for tracking what actions an admin performed.
     */
    Page<AuditLogEntity> findByPerformedByOrderByCreatedAtDesc(UUID performedBy, Pageable pageable);
    
    /**
     * Find all audit logs for a specific action type.
     * Example: Find all LOGIN_FAILED attempts to detect attacks.
     */
    List<AuditLogEntity> findByActionAndCreatedAtAfter(String action, LocalDateTime after);
    
    /**
     * Find audit logs by entity (e.g., all logs for a specific order).
     */
    Page<AuditLogEntity> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
        String entityType, 
        UUID entityId, 
        Pageable pageable
    );
    
    /**
     * Search audit logs with filters (admin audit viewer).
     * Interview Tip: JPQL (Java Persistence Query Language) is database-agnostic.
     * This query works on PostgreSQL, MySQL, Oracle, etc.
     */
    @Query("SELECT a FROM AuditLogEntity a WHERE " +
           "(:userId IS NULL OR a.userId = :userId) AND " +
           "(:performedBy IS NULL OR a.performedBy = :performedBy) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:entityType IS NULL OR a.entityType = :entityType) AND " +
           "(:fromDate IS NULL OR a.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR a.createdAt <= :toDate) " +
           "ORDER BY a.createdAt DESC")
    Page<AuditLogEntity> searchAuditLogs(
        @Param("userId") UUID userId,
        @Param("performedBy") UUID performedBy,
        @Param("action") String action,
        @Param("entityType") String entityType,
        @Param("fromDate") LocalDateTime fromDate,
        @Param("toDate") LocalDateTime toDate,
        Pageable pageable
    );
    
    /**
     * Count failed login attempts for a user in the last X minutes.
     * Used for detecting brute force attacks.
     */
    @Query("SELECT COUNT(a) FROM AuditLogEntity a WHERE " +
           "a.userId = :userId AND " +
           "a.action = 'USER_LOGIN_FAILED' AND " +
           "a.createdAt >= :since")
    long countFailedLoginAttempts(
        @Param("userId") UUID userId,
        @Param("since") LocalDateTime since
    );
}
