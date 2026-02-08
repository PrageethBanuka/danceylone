import api from '@/lib/api';

/**
 * Admin Service
 * Centralized API calls for admin dashboard
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

      // TODO: Fetch users count when user management endpoint is available
      const totalUsers = 0;

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
   * Get all users
   */
  async getUsers(): Promise<User[]> {
    try {
      const response = await api.get('/api/users');
      return response.data;
    } catch (error) {
      console.error('Error fetching users:', error);
      return [];
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
