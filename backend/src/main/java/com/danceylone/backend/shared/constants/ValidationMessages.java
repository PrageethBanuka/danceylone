package com.danceylone.backend.shared.constants;

/**
 * Validation error messages
 * 
 * PRODUCTION BEST PRACTICE: Centralize user-facing messages
 * 
 * Benefits:
 * - Consistent error messages
 * - Easy to update all messages
 * - Easier to internationalize (i18n) later
 * - Better UX with clear, helpful messages
 */
public final class ValidationMessages {
    
    private ValidationMessages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // Authentication
    public static final String INVALID_CREDENTIALS = "Invalid email or password";
    public static final String EMAIL_ALREADY_EXISTS = "Email address is already registered";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String UNAUTHORIZED_ACCESS = "You don't have permission to access this resource";
    
    // Password validation
    public static final String PASSWORD_TOO_SHORT = "Password must be at least " + SecurityConstants.MIN_PASSWORD_LENGTH + " characters";
    public static final String PASSWORD_TOO_LONG = "Password must not exceed " + SecurityConstants.MAX_PASSWORD_LENGTH + " characters";
    public static final String PASSWORD_NO_UPPERCASE = "Password must contain at least one uppercase letter";
    public static final String PASSWORD_NO_LOWERCASE = "Password must contain at least one lowercase letter";
    public static final String PASSWORD_NO_DIGIT = "Password must contain at least one number";
    public static final String PASSWORD_NO_SPECIAL = "Password must contain at least one special character";
    
    // Email validation
    public static final String EMAIL_INVALID = "Invalid email address format";
    public static final String EMAIL_REQUIRED = "Email is required";
    
    // Product validation
    public static final String PRODUCT_NOT_FOUND = "Product not found";
    public static final String PRODUCT_NAME_REQUIRED = "Product name is required";
    public static final String PRODUCT_DESCRIPTION_REQUIRED = "Product description is required";
    public static final String PRODUCT_PRICE_INVALID = "Price must be greater than zero";
    public static final String PRODUCT_STOCK_INVALID = "Stock quantity must be zero or greater";
    public static final String PRODUCT_CATEGORY_REQUIRED = "Category is required";
    
    // General
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later.";
    public static final String INVALID_REQUEST = "Invalid request data";
}
