package com.danceylone.backend.shared.infrastructure.persistence;

import com.danceylone.backend.shared.domain.AuditLog;
import com.danceylone.backend.shared.domain.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter between domain layer (AuditLog) and persistence layer (AuditLogEntity).
 * 
 * Interview Tip: This is the Adapter Pattern from Gang of Four.
 * - Domain layer defines AuditLogRepository interface
 * - Infrastructure layer implements it using JPA
 * - Allows changing persistence technology without touching domain code
 */
@Repository
public class AuditLogRepositoryImpl implements AuditLogRepository {
    
    private final JpaAuditLogRepository jpaRepository;
    
    public AuditLogRepositoryImpl(JpaAuditLogRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public AuditLog save(AuditLog auditLog) {
        AuditLogEntity entity = toEntity(auditLog);
        AuditLogEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public Page<AuditLog> findByUserId(UUID userId, Pageable pageable) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toDomain);
    }
    
    @Override
    public Page<AuditLog> findByPerformedBy(UUID performedBy, Pageable pageable) {
        return jpaRepository.findByPerformedByOrderByCreatedAtDesc(performedBy, pageable)
                .map(this::toDomain);
    }
    
    @Override
    public List<AuditLog> findByActionAfter(String action, LocalDateTime after) {
        return jpaRepository.findByActionAndCreatedAtAfter(action, after)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<AuditLog> searchAuditLogs(
            UUID userId,
            UUID performedBy,
            String action,
            String entityType,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    ) {
        return jpaRepository.searchAuditLogs(
                userId, performedBy, action, entityType, fromDate, toDate, pageable
        ).map(this::toDomain);
    }
    
    @Override
    public long countFailedLoginAttempts(UUID userId, LocalDateTime since) {
        return jpaRepository.countFailedLoginAttempts(userId, since);
    }
    
    /**
     * Convert JPA entity to domain model.
     */
    private AuditLog toDomain(AuditLogEntity entity) {
        return new AuditLog(
                entity.getId(),
                entity.getUserId(),
                entity.getPerformedBy(),
                entity.getAction(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getDetails(),
                entity.getIpAddress(),
                entity.getUserAgent(),
                entity.getCreatedAt()
        );
    }
    
    /**
     * Convert domain model to JPA entity.
     */
    private AuditLogEntity toEntity(AuditLog auditLog) {
        return new AuditLogEntity(
                auditLog.getId(),
                auditLog.getUserId(),
                auditLog.getPerformedBy(),
                auditLog.getAction(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getDetails(),
                auditLog.getIpAddress(),
                auditLog.getUserAgent(),
                auditLog.getCreatedAt()
        );
    }
}
