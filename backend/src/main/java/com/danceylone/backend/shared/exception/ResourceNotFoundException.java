package com.danceylone.backend.shared.exception;

/**
 * Exception thrown when a requested resource is not found
 * 
 * PRODUCTION BEST PRACTICE: Custom exceptions for specific scenarios
 * 
 * Benefits:
 * - Clear exception hierarchy
 * - Specific error handling
 * - Better logging and debugging
 * - Easier to return appropriate HTTP status codes
 */
public class ResourceNotFoundException extends RuntimeException {
    
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }
    
    public String getResourceName() {
        return resourceName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getFieldValue() {
        return fieldValue;
    }
}
