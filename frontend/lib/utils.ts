/**
 * Utility Functions
 * 
 * PRODUCTION BEST PRACTICE: Reusable helper functions
 * - DRY (Don't Repeat Yourself)
 * - Tested and reliable
 * - Type-safe
 */

import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"
import { STORAGE_KEYS } from './constants';
import type { User, CartItem } from '@/types';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

/**
 * Format price as currency
 */
export function formatPrice(price: number): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(price);
}

/**
 * Truncate text with ellipsis
 */
export function truncateText(text: string, maxLength: number): string {
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
}

/**
 * Check if user is authenticated
 */
export function isAuthenticated(): boolean {
  if (typeof window === 'undefined') return false;
  return !!localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
}

/**
 * Get current user from localStorage
 */
export function getCurrentUser(): User | null {
  if (typeof window === 'undefined') return null;
  
  const userStr = localStorage.getItem(STORAGE_KEYS.USER);
  if (!userStr) return null;
  
  try {
    return JSON.parse(userStr);
  } catch {
    return null;
  }
}

/**
 * Check if user has a specific role
 */
export function hasRole(role: string): boolean {
  const user = getCurrentUser();
  return user?.roles?.includes(role) || false;
}

/**
 * Check if user is admin
 */
export function isAdmin(): boolean {
  return hasRole('ADMIN');
}

/**
 * Get cart items from localStorage
 */
export function getCartItems(): CartItem[] {
  if (typeof window === 'undefined') return [];
  
  const cart = localStorage.getItem(STORAGE_KEYS.CART);
  if (!cart) return [];
  
  try {
    return JSON.parse(cart);
  } catch {
    return [];
  }
}

/**
 * Get total cart item count
 */
export function getCartCount(): number {
  const items = getCartItems();
  return items.reduce((sum, item) => sum + item.quantity, 0);
}

/**
 * Get total cart value
 */
export function getCartTotal(): number {
  const items = getCartItems();
  return items.reduce((sum, item) => sum + item.price * item.quantity, 0);
}

/**
 * Validate email format
 */
export function isValidEmail(email: string): boolean {
  const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
  return emailRegex.test(email);
}

/**
 * Validate password strength
 */
export function validatePassword(password: string): { valid: boolean; errors: string[] } {
  const errors: string[] = [];
  
  if (password.length < 8) {
    errors.push('Password must be at least 8 characters');
  }
  
  if (!/[A-Z]/.test(password)) {
    errors.push('Password must contain at least one uppercase letter');
  }
  
  if (!/[a-z]/.test(password)) {
    errors.push('Password must contain at least one lowercase letter');
  }
  
  if (!/\d/.test(password)) {
    errors.push('Password must contain at least one number');
  }
  
  return {
    valid: errors.length === 0,
    errors,
  };
}

/**
 * Safely parse JSON
 */
export function safeJsonParse<T>(json: string, fallback: T): T {
  try {
    return JSON.parse(json);
  } catch {
    return fallback;
  }
}

/**
 * Capitalize first letter
 */
export function capitalize(text: string): string {
  return text.charAt(0).toUpperCase() + text.slice(1).toLowerCase();
}

/**
 * Get error message from API error
 */
export function getErrorMessage(error: any): string {
  if (error.response?.data?.message) {
    return error.response.data.message;
  }
  
  if (error.response?.data?.errors) {
    const errors = error.response.data.errors;
    return Object.values(errors)[0] as string;
  }
  
  if (error.message) {
    return error.message;
  }
  
  return 'An unexpected error occurred';
}
