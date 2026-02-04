"use client";

import { useState, useEffect } from "react";
import Image from "next/image";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { ShoppingCart, Shield } from "lucide-react";
import { toast } from "@/lib/toast";

interface NavbarProps {
  onLoginClick: () => void;
  onRegisterClick: () => void;
  onCartClick: () => void;
}

export default function Navbar({ onLoginClick, onRegisterClick, onCartClick }: NavbarProps) {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [cartItemCount, setCartItemCount] = useState(0);

  useEffect(() => {
    const token = localStorage.getItem("authToken");
    setIsLoggedIn(!!token);
    
    // Check if user is admin
    const userStr = localStorage.getItem("user");
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        setIsAdmin(user.roles?.includes('ADMIN') || false);
      } catch (err) {
        setIsAdmin(false);
      }
    }
    
    // Load cart count
    updateCartCount();
    
    // Listen for cart updates
    window.addEventListener('cartUpdated', updateCartCount);
    return () => window.removeEventListener('cartUpdated', updateCartCount);
  }, []);

  const updateCartCount = () => {
    const cart = localStorage.getItem('cart');
    if (cart) {
      const items = JSON.parse(cart);
      const count = items.reduce((sum: number, item: any) => sum + item.quantity, 0);
      setCartItemCount(count);
    } else {
      setCartItemCount(0);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("userEmail");
    localStorage.removeItem("user");
    localStorage.removeItem("token");
    setIsLoggedIn(false);
    setIsAdmin(false);
    toast.info("Logged out", "See you next time!");
    window.location.reload();
  };

  return (
    <nav className="bg-white/80 backdrop-blur-md border-b border-neutral-200 fixed w-full top-0 z-40">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center gap-8">
            <Link href="/" className="flex items-center">
              <div className="relative h-14 w-40 overflow-hidden rounded-lg">
                <Image
                  src="/images/logo.jpg"
                  alt="Danceylone"
                  fill
                  className="object-cover object-center cursor-pointer"
                />
              </div>
            </Link>
            {isAdmin && (
              <Link href="/admin" className="text-sm font-medium text-neutral-700 hover:text-black transition flex items-center gap-1">
                <Shield className="w-4 h-4" />
                Admin
              </Link>
            )}
            <Link href="/products" className="text-sm font-medium text-neutral-700 hover:text-black transition">
              Products
            </Link>
          </div>
          <div className="flex items-center gap-3">
            {/* Cart Button */}
            <button
              onClick={onCartClick}
              className="relative p-2 hover:bg-neutral-100 rounded-full transition"
            >
              <ShoppingCart className="w-5 h-5" />
              {cartItemCount > 0 && (
                <span className="absolute -top-1 -right-1 bg-black text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                  {cartItemCount}
                </span>
              )}
            </button>

            {isLoggedIn ? (
              <>
                <span className="text-sm text-neutral-600 hidden sm:block">
                  {localStorage.getItem("userEmail")}
                </span>
                <Button
                  onClick={handleLogout}
                  variant="ghost"
                  className="text-sm font-medium text-neutral-700 hover:text-black"
                >
                  Sign Out
                </Button>
              </>
            ) : (
              <>
                <Button
                  onClick={onLoginClick}
                  variant="ghost"
                  className="text-sm font-medium text-neutral-700 hover:text-black"
                >
                  Sign In
                </Button>
                <Button
                  onClick={onRegisterClick}
                  className="bg-black text-white hover:bg-neutral-800 rounded-full px-5 h-9 text-sm font-medium"
                >
                  Get Started
                </Button>
              </>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
