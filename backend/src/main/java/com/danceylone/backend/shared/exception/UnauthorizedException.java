package com.danceylone.backend.shared.exception;

/**
 * Exception thrown when user lacks required permissions
 * 
 * Use this for authorization failures (403 Forbidden)
 * vs BadCredentialsException for authentication failures (401 Unauthorized)
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
