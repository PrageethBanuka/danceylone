'use client';

import { useRouter } from 'next/navigation';
import { Package, ShoppingCart, Users, TrendingUp } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';

/**
 * Admin Dashboard Home
 * 
 * LEARNING POINTS FOR INTERNSHIP:
 * 
 * 1. DASHBOARD DESIGN
 *    - Overview cards with key metrics
 *    - Quick action buttons
 *    - Visual hierarchy with cards
 * 
 * 2. NAVIGATION PATTERNS
 *    - Central hub to admin features
 *    - Clear call-to-actions
 *    - Icon + text for clarity
 */

export default function AdminDashboard() {
  const router = useRouter();

  return (
    <div className="space-y-8">
      {/* Welcome Section */}
      <div>
        <h2 className="text-3xl font-bold mb-2">Welcome to Admin Dashboard</h2>
        <p className="text-neutral-600">Manage your Danceylone store</p>
      </div>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Products</CardTitle>
            <Package className="h-4 w-4 text-neutral-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">4</div>
            <p className="text-xs text-neutral-600 mt-1">Active in catalog</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Orders</CardTitle>
            <ShoppingCart className="h-4 w-4 text-neutral-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">0</div>
            <p className="text-xs text-neutral-600 mt-1">Pending fulfillment</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Users</CardTitle>
            <Users className="h-4 w-4 text-neutral-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">-</div>
            <p className="text-xs text-neutral-600 mt-1">Registered customers</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Revenue</CardTitle>
            <TrendingUp className="h-4 w-4 text-neutral-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">$0</div>
            <p className="text-xs text-neutral-600 mt-1">Total sales</p>
          </CardContent>
        </Card>
      </div>

      {/* Quick Actions */}
      <div>
        <h3 className="text-xl font-semibold mb-4">Quick Actions</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <Card className="hover:shadow-lg transition-shadow cursor-pointer" onClick={() => router.push('/admin/products')}>
            <CardHeader>
              <Package className="h-8 w-8 mb-2" />
              <CardTitle>Manage Products</CardTitle>
              <CardDescription>
                Add, edit, or remove products from your catalog
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button className="w-full">Go to Products</Button>
            </CardContent>
          </Card>

          <Card className="hover:shadow-lg transition-shadow opacity-50">
            <CardHeader>
              <ShoppingCart className="h-8 w-8 mb-2" />
              <CardTitle>View Orders</CardTitle>
              <CardDescription>
                Track and manage customer orders
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button className="w-full" disabled>Coming Soon</Button>
            </CardContent>
          </Card>

          <Card className="hover:shadow-lg transition-shadow opacity-50">
            <CardHeader>
              <Users className="h-8 w-8 mb-2" />
              <CardTitle>Manage Users</CardTitle>
              <CardDescription>
                View and manage customer accounts
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button className="w-full" disabled>Coming Soon</Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
