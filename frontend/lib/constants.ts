/**
 * Frontend Constants
 * 
 * PRODUCTION BEST PRACTICE: Centralize magic values
 * - Single source of truth
 * - Easy to update
 * - Type-safe
 */

// API
export const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
export const API_TIMEOUT = 10000; // 10 seconds

// Local Storage Keys
export const STORAGE_KEYS = {
  AUTH_TOKEN: 'authToken',
  TOKEN: 'token',
  USER: 'user',
  USER_EMAIL: 'userEmail',
  CART: 'cart',
} as const;

// Routes
export const ROUTES = {
  HOME: '/',
  PRODUCTS: '/products',
  PRODUCT_DETAIL: (id: string) => `/products/${id}`,
  ADMIN: '/admin',
  ADMIN_PRODUCTS: '/admin/products',
} as const;

// Product Categories
export const PRODUCT_CATEGORIES = [
  'All',
  'Shoes',
  'Dancewear',
  'Accessories',
] as const;

// UI
export const UI_CONSTANTS = {
  DEBOUNCE_DELAY: 400,
  CART_ANIMATION_DELAY: 300,
  MAX_TOAST_DURATION: 5000,
} as const;

// Validation
export const VALIDATION = {
  PASSWORD_MIN_LENGTH: 8,
  PASSWORD_MAX_LENGTH: 128,
  EMAIL_PATTERN: /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/,
} as const;

// Error Messages
export const ERROR_MESSAGES = {
  NETWORK_ERROR: 'Network error. Please check your connection.',
  UNAUTHORIZED: 'Please login to continue.',
  FORBIDDEN: 'You don\'t have permission to access this resource.',
  NOT_FOUND: 'Resource not found.',
  SERVER_ERROR: 'Something went wrong. Please try again later.',
  VALIDATION_ERROR: 'Please check your input and try again.',
} as const;

// Success Messages
export const SUCCESS_MESSAGES = {
  LOGIN_SUCCESS: 'Successfully logged in!',
  REGISTER_SUCCESS: 'Registration successful! Please login.',
  PRODUCT_CREATED: 'Product created successfully!',
  PRODUCT_UPDATED: 'Product updated successfully!',
  PRODUCT_DELETED: 'Product deleted successfully!',
  ADDED_TO_CART: 'Added to cart!',
} as const;
