package com.danceylone.backend.user.domain;

/**
 * User Role Enum
 * 
 * PRODUCTION BEST PRACTICE: Use enums instead of strings for fixed sets of values
 * 
 * Benefits:
 * - Type safety at compile time
 * - Prevents typos and invalid values
 * - IDE autocomplete
 * - Centralized role definitions
 * - Easy to add new roles
 * 
 * INTERNSHIP TIP: Enums are essential for maintainable enterprise applications
 */
public enum Role {
    /**
     * Standard user role - default for all registered users
     */
    USER,
    
    /**
     * Administrator role - full system access
     */
    ADMIN,
    
    /**
     * Customer support role - can view orders and assist customers
     */
    SUPPORT,
    
    /**
     * Inventory manager role - can manage products and stock
     */
    INVENTORY_MANAGER;
    
    /**
     * Get role name as string for storage/serialization
     */
    public String getRoleName() {
        return this.name();
    }
    
    /**
     * Parse role from string safely
     * @param roleName the role name string
     * @return Role enum or null if invalid
     */
    public static Role fromString(String roleName) {
        try {
            return Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
