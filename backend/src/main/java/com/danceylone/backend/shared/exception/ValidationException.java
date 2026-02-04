package com.danceylone.backend.shared.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception for validation errors with field-level details
 * 
 * PRODUCTION BEST PRACTICE: Provide detailed validation feedback
 * 
 * Benefits:
 * - Frontend can show field-specific errors
 * - Better UX with clear guidance
 * - Easy to debug validation issues
 */
public class ValidationException extends RuntimeException {
    
    private final List<FieldError> errors;
    
    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
    }
    
    public ValidationException(List<FieldError> errors) {
        super("Validation failed");
        this.errors = errors;
    }
    
    public ValidationException(String field, String message) {
        super(message);
        this.errors = List.of(new FieldError(field, message));
    }
    
    public List<FieldError> getErrors() {
        return errors;
    }
    
    public static class FieldError {
        private final String field;
        private final String message;
        
        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
        
        public String getField() {
            return field;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
