import api from '@/lib/api';

/**
 * Admin Service - Production Ready
 * 
 * ARCHITECTURAL PATTERNS:
 * - Service layer abstracts API calls
 * - TypeScript interfaces for type safety
 * - Error handling and fallbacks
 * - Pagination support for scalability
 * 
 * INTERVIEW TIP: Explain service layer pattern
 * "Service layer separates business logic from UI components"
 * "Makes testing easier - mock the service, not the API"
 */

export interface DashboardStats {
  totalProducts: number;
  totalOrders: number;
  totalUsers: number;
  totalRevenue: number;
  pendingOrders: number;
}

export interface Order {
  id: string;
  orderNumber: string;
  userId: string;
  status: string;
  totalAmount: number;
  shippingAddress: string;
  createdAt: string;
}

/**
 * User Response DTO
 * Matches backend UserResponse.java
 */
export interface UserResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
  active: boolean;
}

/**
 * Generic Page Response
 * PRODUCTION: Standard pagination response
 * 
 * INTERVIEW TIP: Generic types make this reusable
 * PageResponse<UserResponse>, PageResponse<Order>, etc.
 */
export interface PageResponse<T> {
  content: T[];              // The actual data
  page: number;              // Current page (0-indexed)
  size: number;              // Items per page
  totalElements: number;     // Total items across all pages
  totalPages: number;        // Total number of pages
  hasNext: boolean;          // Can we go forward?
  hasPrevious: boolean;      // Can we go back?
  sortBy: string;            // What field we sorted by
  direction: string;         // ASC or DESC
}

/**
 * User Query Parameters
 * Makes API calls type-safe and self-documenting
 */
export interface UserQueryParams {
  page?: number;
  size?: number;
  sortBy?: string;
  direction?: 'ASC' | 'DESC';
  search?: string;
  role?: string;
}

// Legacy User interface (for backward compatibility)
export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

export const adminService = {
  /**
   * Get dashboard statistics
   * BACKWARD COMPATIBLE: Uses paginated API but returns simple stats
   */
  async getDashboardStats(): Promise<DashboardStats> {
    try {
      // Fetch products count
      const productsResponse = await api.get('/api/products');
      const totalProducts = productsResponse.data.length;

      // Fetch orders (if endpoint exists)
      let totalOrders = 0;
      let pendingOrders = 0;
      let totalRevenue = 0;

      try {
        const ordersResponse = await api.get('/api/orders/all');
        totalOrders = ordersResponse.data.length;
        pendingOrders = ordersResponse.data.filter((o: Order) => o.status === 'PENDING').length;
        totalRevenue = ordersResponse.data.reduce((sum: number, o: Order) => sum + o.totalAmount, 0);
      } catch {
        // Orders endpoint might not be implemented yet
      }

      // Fetch users count from paginated API
      let totalUsers = 0;
      try {
        const usersResponse = await this.getUsers({ page: 0, size: 1 }); // Fetch just 1 to get totalElements
        totalUsers = usersResponse.totalElements;
      } catch {
        // Users endpoint might not be accessible
      }

      return {
        totalProducts,
        totalOrders,
        totalUsers,
        totalRevenue,
        pendingOrders
      };
    } catch (error) {
      console.error('Error fetching dashboard stats:', error);
      throw error;
    }
  },

  /**
   * Get all orders
   */
  async getOrders(): Promise<Order[]> {
    try {
      const response = await api.get('/api/orders/all');
      return response.data;
    } catch (error) {
      console.error('Error fetching orders:', error);
      return [];
    }
  },

  /**
   * Get users with pagination (PHASE 2)
   * 
   * PRODUCTION PATTERN: Query parameters as object
   * - Makes API calls self-documenting
   * - Easy to add new parameters
   * - Type-safe with TypeScript
   * 
   * INTERVIEW TIP: Explain REST API query parameters
   * "Query params for filtering/pagination (GET)"
   * "Request body for creating/updating (POST/PUT)"
   */
  async getUsers(params?: UserQueryParams): Promise<PageResponse<UserResponse>> {
    try {
      // Build query string from parameters
      const queryParams = new URLSearchParams();
      
      if (params?.page !== undefined) queryParams.append('page', params.page.toString());
      if (params?.size) queryParams.append('size', params.size.toString());
      if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
      if (params?.direction) queryParams.append('direction', params.direction);
      if (params?.search) queryParams.append('search', params.search);
      if (params?.role) queryParams.append('role', params.role);
      
      const url = `/api/users${queryParams.toString() ? '?' + queryParams.toString() : ''}`;
      const response = await api.get<PageResponse<UserResponse>>(url);
      
      return response.data;
    } catch (error) {
      console.error('Error fetching users:', error);
      throw error;
    }
  },

  /**
   * Get user by ID
   * PRODUCTION: Individual user details
   */
  async getUserById(userId: string): Promise<UserResponse> {
    try {
      const response = await api.get<UserResponse>(`/api/users/${userId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching user ${userId}:`, error);
      throw error;
    }
  },

  /**
   * Update order status
   */
  async updateOrderStatus(orderId: string, status: string): Promise<void> {
    await api.patch(`/api/orders/${orderId}/status`, { status });
  },
};

export default adminService;
