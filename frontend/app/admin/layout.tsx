'use client';

import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { toast } from '@/lib/toast';

/**
 * Admin Layout Component
 * 
 * LEARNING POINTS FOR INTERNSHIP:
 * 
 * 1. PROTECTED ROUTES
 *    - Check authentication before rendering admin pages
 *    - Redirect unauthorized users to login
 *    - Critical security pattern
 * 
 * 2. LAYOUT PATTERN
 *    - Shared sidebar for admin navigation
 *    - Consistent admin UI across pages
 *    - Children prop for nested routes
 * 
 * 3. ROLE-BASED ACCESS CONTROL (RBAC)
 *    - Check if user has admin role
 *    - Different UI/features per role
 *    - Essential for enterprise apps
 */

export default function AdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const router = useRouter();
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Check if user is logged in and has admin role
    const token = localStorage.getItem('token');
    const userStr = localStorage.getItem('user');
    
    if (!token || !userStr) {
      // Not logged in - redirect to home with login modal
      router.push('/');
      window.dispatchEvent(new CustomEvent('openLogin'));
      return;
    }

    try {
      const user = JSON.parse(userStr);
      
      // Check if user has ADMIN role
      if (!user.roles || !user.roles.includes('ADMIN')) {
        toast.error('Access Denied', 'Admin privileges required');
        router.push('/');
        return;
      }

      setIsAuthenticated(true);
    } catch (err) {
      console.error('Invalid user data:', err);
      router.push('/');
    } finally {
      setIsLoading(false);
    }
  }, [router]);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-black"></div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return null;
  }

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Admin Header */}
      <header className="bg-black text-white py-4 px-6 shadow-lg">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <h1 className="text-2xl font-bold">Admin Dashboard</h1>
          <button
            onClick={() => {
              localStorage.removeItem('token');
              localStorage.removeItem('user');
              router.push('/');
            }}
            className="bg-white text-black px-4 py-2 rounded hover:bg-neutral-200 transition-colors"
          >
            Logout
          </button>
        </div>
      </header>

      <div className="max-w-7xl mx-auto p-6">
        {children}
      </div>
    </div>
  );
}
