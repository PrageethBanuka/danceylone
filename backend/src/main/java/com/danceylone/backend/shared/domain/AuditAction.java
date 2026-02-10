package com.danceylone.backend.shared.domain;

/**
 * Audit Action Enum
 * 
 * PRODUCTION PATTERN: Standardized Action Names
 * 
 * WHY ENUM INSTEAD OF STRINGS?
 * 1. Type safety: Compiler catches typos
 * 2. Consistency: "USER_CREATED" not "user-created" or "UserCreated"
 * 3. Searchability: Easy to find all login attempts
 * 4. Documentation: Self-documenting code
 * 
 * INTERVIEW TIP: Explain enum benefits
 * "Enums provide compile-time safety and prevent magic strings"
 * "Makes codebase grep-able for security investigations"
 * 
 * NAMING CONVENTION: ENTITY_ACTION
 * - USER_CREATED, USER_UPDATED, USER_DELETED
 * - ORDER_PLACED, ORDER_SHIPPED, ORDER_CANCELLED
 * - Makes grouping by entity easy in logs
 */
public enum AuditAction {
    
    // ===== USER LIFECYCLE ACTIONS =====
    USER_CREATED("User account created"),
    USER_UPDATED("User profile updated"),
    USER_DELETED("User account deleted"),
    
    // ===== USER STATUS ACTIONS (Phase 3) =====
    USER_ACTIVATED("User account activated"),
    USER_DEACTIVATED("User account deactivated"),
    USER_LOCKED("User account locked"),
    USER_UNLOCKED("User account unlocked"),
    
    // ===== AUTHENTICATION ACTIONS =====
    USER_LOGIN_SUCCESS("User logged in successfully"),
    USER_LOGIN_FAILED("User login attempt failed"),
    USER_LOGOUT("User logged out"),
    USER_PASSWORD_CHANGED("User password changed"),
    USER_PASSWORD_RESET_REQUESTED("Password reset requested"),
    USER_PASSWORD_RESET_COMPLETED("Password reset completed"),
    
    // ===== EMAIL VERIFICATION ACTIONS (Phase 3) =====
    USER_EMAIL_VERIFICATION_SENT("Email verification sent"),
    USER_EMAIL_VERIFIED("Email address verified"),
    USER_EMAIL_CHANGED("Email address changed"),
    
    // ===== ROLE MANAGEMENT ACTIONS =====
    USER_ROLE_ADDED("Role added to user"),
    USER_ROLE_REMOVED("Role removed from user"),
    USER_PERMISSIONS_CHANGED("User permissions modified"),
    
    // ===== SECURITY ACTIONS =====
    USER_SESSION_TERMINATED("User session terminated"),
    USER_SUSPICIOUS_ACTIVITY("Suspicious activity detected"),
    USER_TWO_FACTOR_ENABLED("Two-factor authentication enabled"),
    USER_TWO_FACTOR_DISABLED("Two-factor authentication disabled"),
    
    // ===== ADMIN ACTIONS =====
    ADMIN_USER_IMPERSONATION("Admin impersonated user"),
    ADMIN_FORCE_PASSWORD_RESET("Admin forced password reset"),
    ADMIN_DATA_EXPORT("Admin exported user data"),
    
    // ===== COMPLIANCE ACTIONS (GDPR) =====
    USER_DATA_EXPORT_REQUESTED("User requested data export"),
    USER_DATA_DELETION_REQUESTED("User requested data deletion"),
    USER_CONSENT_GIVEN("User gave consent for data processing"),
    USER_CONSENT_WITHDRAWN("User withdrew consent"),
    
    // ===== FUTURE: ORDER ACTIONS =====
    ORDER_CREATED("Order placed"),
    ORDER_UPDATED("Order updated"),
    ORDER_CANCELLED("Order cancelled"),
    ORDER_SHIPPED("Order shipped"),
    ORDER_DELIVERED("Order delivered"),
    ORDER_REFUNDED("Order refunded");
    
    private final String description;
    
    AuditAction(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if action is security-related
     * INTERVIEW: "We can flag high-risk actions for immediate alerting"
     */
    public boolean isSecurityAction() {
        return this == USER_LOGIN_FAILED 
            || this == USER_LOCKED
            || this == USER_SUSPICIOUS_ACTIVITY
            || this == ADMIN_USER_IMPERSONATION
            || this == ADMIN_FORCE_PASSWORD_RESET;
    }
    
    /**
     * Check if action is compliance-related (GDPR)
     * INTERVIEW: "GDPR requires special handling of certain actions"
     */
    public boolean isComplianceAction() {
        return this == USER_DATA_EXPORT_REQUESTED
            || this == USER_DATA_DELETION_REQUESTED
            || this == USER_CONSENT_GIVEN
            || this == USER_CONSENT_WITHDRAWN
            || this == ADMIN_DATA_EXPORT;
    }
}
