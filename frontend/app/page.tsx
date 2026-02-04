"use client";

import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";

export default function Home() {
  const handleLoginClick = () => {
    window.dispatchEvent(new Event('openLogin'));
  };

  const handleRegisterClick = () => {
    window.dispatchEvent(new Event('openRegister'));
  };

  return (
    <div className="min-h-screen bg-white">

      {/* Hero Section */}
      <section className="pt-32 pb-24 px-4 sm:px-6 lg:px-8">
        <div className="max-w-6xl mx-auto text-center">
          <h1 className="text-6xl md:text-7xl font-semibold tracking-tight text-black mb-6">
            Move with purpose.
          </h1>
          <p className="text-xl md:text-2xl text-neutral-600 mb-12 max-w-2xl mx-auto font-light">
            Premium dancewear and accessories for those who live to dance.
          </p>
          <div className="flex gap-4 justify-center">
            <Button
              size="lg"
              onClick={handleRegisterClick}
              className="bg-black text-white hover:bg-neutral-800 rounded-full px-8 h-12 text-base font-medium"
            >
              Get Started
            </Button>
            <Button
              size="lg"
              variant="outline"
              className="rounded-full px-8 h-12 text-base font-medium border-neutral-300 hover:bg-neutral-50"
            >
              Browse Collection
            </Button>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-24 bg-neutral-50">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <Card className="p-8 border-0 shadow-sm bg-white hover:shadow-md transition-shadow">
              <div className="w-12 h-12 bg-black rounded-full flex items-center justify-center mb-6">
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M5 13l4 4L19 7" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold mb-3 text-black">Premium Quality</h3>
              <p className="text-neutral-600 font-light leading-relaxed">
                Meticulously crafted dancewear from world-renowned brands.
              </p>
            </Card>

            <Card className="p-8 border-0 shadow-sm bg-white hover:shadow-md transition-shadow">
              <div className="w-12 h-12 bg-black rounded-full flex items-center justify-center mb-6">
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold mb-3 text-black">Express Delivery</h3>
              <p className="text-neutral-600 font-light leading-relaxed">
                Swift and reliable shipping to keep you moving.
              </p>
            </Card>

            <Card className="p-8 border-0 shadow-sm bg-white hover:shadow-md transition-shadow">
              <div className="w-12 h-12 bg-black rounded-full flex items-center justify-center mb-6">
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-.095c-.5 0-.905.405-.905.905 0 .714-.211 1.412-.608 2.006L7 11v9m7-10h-2M7 20H5a2 2 0 01-2-2v-6a2 2 0 012-2h2.5" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold mb-3 text-black">Expert Support</h3>
              <p className="text-neutral-600 font-light leading-relaxed">
                Dedicated team ready to help with every step.
              </p>
            </Card>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-32 bg-black">
        <div className="max-w-4xl mx-auto text-center px-4">
          <h2 className="text-5xl md:text-6xl font-semibold text-white mb-6 tracking-tight">
            Your journey begins here.
          </h2>
          <p className="text-xl text-neutral-400 mb-12 font-light">
            Join the community of dancers who demand excellence.
          </p>
          <Button
            size="lg"
            onClick={handleRegisterClick}
            className="bg-white text-black hover:bg-neutral-100 rounded-full px-10 h-14 text-lg font-medium"
          >
            Create Account
          </Button>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-white border-t border-neutral-200 py-12">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8 mb-12">
            <div>
              <h4 className="font-medium mb-4 text-sm text-black">Shop</h4>
              <ul className="space-y-3 text-sm">
                <li><a href="#" className="text-neutral-600 hover:text-black transition">Dancewear</a></li>
                <li><a href="#" className="text-neutral-600 hover:text-black transition">Footwear</a></li>
                <li><a href="#" className="text-neutral-600 hover:text-black transition">Accessories</a></li>
              </ul>
            </div>
            <div>
              <h4 className="font-medium mb-4 text-sm text-black">Support</h4>
              <ul className="space-y-3 text-sm">
                <li><a href="#" className="text-neutral-600 hover:text-black transition">Contact</a></li>
                <li><a href="#" className="text-neutral-600 hover:text-black transition">FAQ</a></li>
                <li><a href="#" className="text-neutral-600 hover:text-black transition">Shipping</a></li>
              </ul>
            </div>
            <div>
              <h4 className="font-medium mb-4 text-sm text-black">Company</h4>
              <ul className="space-y-3 text-sm">
                <li><a href="#" className="text-neutral-600 hover:text-black transition">About</a></li>
                <li><a href="#" className="text-neutral-600 hover:text-black transition">Careers</a></li>
                <li><a href="#" className="text-neutral-600 hover:text-black transition">Press</a></li>
              </ul>
            </div>
            <div>
              <h4 className="font-medium mb-4 text-sm text-black">Legal</h4>
              <ul className="space-y-3 text-sm">
                <li><a href="#" className="text-neutral-600 hover:text-black transition">Privacy</a></li>
                <li><a href="#" className="text-neutral-600 hover:text-black transition">Terms</a></li>
              </ul>
            </div>
          </div>
          <div className="pt-8 border-t border-neutral-200 text-center">
            <p className="text-sm text-neutral-500">&copy; 2026 Danceylone. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
}
