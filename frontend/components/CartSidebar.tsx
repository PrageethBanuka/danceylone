'use client';

import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { X } from 'lucide-react';

interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
  imageUrl: string;
}

interface CartSidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function CartSidebar({ isOpen, onClose }: CartSidebarProps) {
  const [cartItems, setCartItems] = useState<CartItem[]>([]);

  useEffect(() => {
    // Load cart from localStorage
    const saved = localStorage.getItem('cart');
    if (saved) {
      setCartItems(JSON.parse(saved));
    }
  }, [isOpen]);

  const total = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

  const removeItem = (id: string) => {
    const updated = cartItems.filter(item => item.id !== id);
    setCartItems(updated);
    localStorage.setItem('cart', JSON.stringify(updated));
    // Dispatch event to update navbar badge in real-time
    window.dispatchEvent(new Event('cartUpdated'));
  };

  const updateQuantity = (id: string, change: number) => {
    const updated = cartItems.map(item => {
      if (item.id === id) {
        const newQuantity = item.quantity + change;
        if (newQuantity <= 0) return null; // Remove if quantity becomes 0
        return { ...item, quantity: newQuantity };
      }
      return item;
    }).filter(Boolean) as CartItem[];
    
    setCartItems(updated);
    localStorage.setItem('cart', JSON.stringify(updated));
    // Dispatch event to update navbar badge in real-time
    window.dispatchEvent(new Event('cartUpdated'));
  };

  return (
    <>
      {/* Backdrop */}
      {isOpen && (
        <div 
          className="fixed inset-0 bg-black/50 z-40 transition-opacity"
          onClick={onClose}
        />
      )}

      {/* Sidebar - FIXED POSITION - Full viewport height */}
      <div 
        className={`fixed top-0 right-0 h-screen w-96 bg-white shadow-2xl z-50 transform transition-transform duration-300 ${
          isOpen ? 'translate-x-0' : 'translate-x-full'
        }`}
      >
        {/* FIXED HEADER - stays at top */}
        <div className="sticky top-0 bg-white border-b border-neutral-200 px-6 py-4 flex items-center justify-between">
          <h2 className="text-2xl font-bold">Shopping Cart</h2>
          <button
            onClick={onClose}
            className="p-2 hover:bg-neutral-100 rounded-full transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* SCROLLABLE CONTENT */}
        <div className="flex flex-col h-[calc(100%-140px)] overflow-y-auto px-6 py-4">
          {cartItems.length === 0 ? (
            <div className="flex-1 flex items-center justify-center">
              <div className="text-center">
                <p className="text-neutral-600 mb-2">Your cart is empty</p>
                <p className="text-sm text-neutral-500">Add some products to get started</p>
              </div>
            </div>
          ) : (
            <div className="space-y-4">
              {cartItems.map((item) => (
                <div key={item.id} className="flex gap-4 pb-4 border-b border-neutral-100">
                  <img 
                    src={item.imageUrl} 
                    alt={item.name}
                    className="w-20 h-20 object-cover rounded"
                  />
                  <div className="flex-1">
                    <h3 className="font-medium text-sm">{item.name}</h3>
                    <p className="text-sm font-bold mt-1">${item.price.toFixed(2)}</p>
                    
                    {/* Quantity Controls */}
                    <div className="flex items-center gap-2 mt-2">
                      <button
                        onClick={() => updateQuantity(item.id, -1)}
                        className="w-6 h-6 flex items-center justify-center border border-neutral-300 rounded hover:bg-neutral-100"
                      >
                        −
                      </button>
                      <span className="text-sm font-medium w-8 text-center">{item.quantity}</span>
                      <button
                        onClick={() => updateQuantity(item.id, 1)}
                        className="w-6 h-6 flex items-center justify-center border border-neutral-300 rounded hover:bg-neutral-100"
                      >
                        +
                      </button>
                    </div>
                  </div>
                  <button
                    onClick={() => removeItem(item.id)}
                    className="text-red-500 hover:text-red-700 text-sm self-start"
                  >
                    <X className="w-4 h-4" />
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* FIXED FOOTER - stays at bottom */}
        <div className="absolute bottom-0 left-0 right-0 bg-white border-t border-neutral-200 px-6 py-4">
          <div className="flex justify-between mb-4">
            <span className="font-bold text-lg">Total:</span>
            <span className="font-bold text-xl">${total.toFixed(2)}</span>
          </div>
          <Button className="w-full" disabled={cartItems.length === 0}>
            Checkout
          </Button>
        </div>
      </div>
    </>
  );
}
