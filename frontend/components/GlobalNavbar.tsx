'use client';

import { useState, useEffect } from 'react';
import Navbar from './Navbar';
import CartSidebar from './CartSidebar';
import AuthModal from './AuthModal';

/**
 * Global Navbar Wrapper
 * 
 * ARCHITECTURE PATTERN: Client Component Wrapper
 * - layout.tsx is a Server Component (can't use hooks)
 * - This wrapper is a Client Component (can use useState)
 * - Manages global state: cart, auth modals
 * 
 * BENEFIT: Navbar + Cart state shared across all pages
 */
export default function GlobalNavbar() {
  const [cartOpen, setCartOpen] = useState(false);
  const [showAuthModal, setShowAuthModal] = useState(false);
  const [authMode, setAuthMode] = useState<'login' | 'register'>('login');

  useEffect(() => {
    // Listen for global auth events from anywhere in the app
    const handleOpenLogin = () => {
      setAuthMode('login');
      setShowAuthModal(true);
    };

    const handleOpenRegister = () => {
      setAuthMode('register');
      setShowAuthModal(true);
    };

    window.addEventListener('openLogin', handleOpenLogin);
    window.addEventListener('openRegister', handleOpenRegister);

    return () => {
      window.removeEventListener('openLogin', handleOpenLogin);
      window.removeEventListener('openRegister', handleOpenRegister);
    };
  }, []);

  const handleLoginClick = () => {
    setAuthMode('login');
    setShowAuthModal(true);
  };

  const handleRegisterClick = () => {
    setAuthMode('register');
    setShowAuthModal(true);
  };

  return (
    <>
      <Navbar 
        onLoginClick={handleLoginClick}
        onRegisterClick={handleRegisterClick}
        onCartClick={() => setCartOpen(true)}
      />
      
      {/* Cart Sidebar - Outside navbar to prevent height clipping */}
      <CartSidebar isOpen={cartOpen} onClose={() => setCartOpen(false)} />
      
      {/* Auth Modal */}
      {showAuthModal && (
        <AuthModal
          mode={authMode}
          onClose={() => setShowAuthModal(false)}
          onSwitchMode={(mode) => setAuthMode(mode)}
        />
      )}
    </>
  );
}
