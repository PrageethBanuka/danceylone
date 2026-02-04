/**
 * TypeScript Types and Interfaces
 * 
 * PRODUCTION BEST PRACTICE: Centralized type definitions
 * - Type safety across the application
 * - Consistent data structures
 * - Better IDE autocomplete
 * - Easier refactoring
 * - Self-documenting code
 */

// User and Authentication
export interface User {
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
  message: string;
}

// Product
export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  category: string;
  imageUrl: string;
  stockQuantity: number;
  available: boolean;
}

export interface CreateProductRequest {
  name: string;
  description: string;
  price: number;
  category: string;
  imageUrl: string;
  stockQuantity: number;
}

export interface UpdateProductRequest extends CreateProductRequest {
  id: string;
}

// Cart
export interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
  imageUrl: string;
}

// API Error Response
export interface ApiError {
  message: string;
  errors?: Record<string, string>;
}

// Form Data (for client-side only)
export interface ProductFormData {
  name: string;
  description: string;
  price: string;
  category: string;
  imageUrl: string;
  stockQuantity: string;
}

// Enums
export enum Role {
  USER = 'USER',
  ADMIN = 'ADMIN',
  SUPPORT = 'SUPPORT',
  INVENTORY_MANAGER = 'INVENTORY_MANAGER'
}

export enum ProductCategory {
  SHOES = 'Shoes',
  DANCEWEAR = 'Dancewear',
  ACCESSORIES = 'Accessories'
}
