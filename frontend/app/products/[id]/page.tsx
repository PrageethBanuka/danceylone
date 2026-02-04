'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { toast } from '@/lib/toast';
import Image from 'next/image';
import api from '@/lib/api';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { ArrowLeft, ShoppingCart, Package, Truck } from 'lucide-react';

/**
 * Product Detail Page
 * 
 * NEXT.JS LEARNING POINTS:
 * 
 * 1. DYNAMIC ROUTING
 *    - File: /products/[id]/page.tsx
 *    - Route: /products/abc-123-uuid
 *    - useParams() extracts the 'id' from URL
 * 
 * 2. DATA FETCHING PATTERNS
 *    - Client-side: useEffect + fetch (this example)
 *    - Server-side: async component + fetch (Next.js 13+)
 *    - This uses client-side for real-time updates
 * 
 * 3. NAVIGATION
 *    - useRouter() for programmatic navigation
 *    - Link component for declarative navigation
 * 
 * INTERNSHIP TIP: Be ready to explain:
 * - Why we use useParams vs props
 * - Client vs Server Components trade-offs
 * - When to use loading states vs Suspense
 */

interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  category: string;
  imageUrl: string;
  stockQuantity: number;
  available: boolean;
}

export default function ProductDetailPage() {
  const params = useParams();
  const router = useRouter();
  const productId = params.id as string;

  const [product, setProduct] = useState<Product | null>(null);
  const [relatedProducts, setRelatedProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [quantity, setQuantity] = useState(1);

  useEffect(() => {
    if (productId) {
      fetchProductDetails();
    }
  }, [productId]);

  const fetchProductDetails = async () => {
    try {
      setLoading(true);
      setError('');

      // Fetch main product
      const response = await api.get(`/api/products/${productId}`);
      setProduct(response.data);

      // Fetch related products (same category)
      const relatedResponse = await api.get(`/api/products?category=${response.data.category}`);
      const filtered = relatedResponse.data.filter((p: Product) => p.id !== productId).slice(0, 4);
      setRelatedProducts(filtered);

    } catch (err) {
      setError('Failed to load product details');
      console.error('Error fetching product:', err);
    } finally {
      setLoading(false);
    }
  };

  const addToCart = () => {
    if (!product) return;

    const cart = JSON.parse(localStorage.getItem('cart') || '[]');
    const existing = cart.find((item: any) => item.id === product.id);

    if (existing) {
      existing.quantity += quantity;
    } else {
      cart.push({
        id: product.id,
        name: product.name,
        price: product.price,
        quantity: quantity,
        imageUrl: product.imageUrl
      });
    }

    localStorage.setItem('cart', JSON.stringify(cart));
    window.dispatchEvent(new Event('cartUpdated'));

    // Visual feedback
    toast.success('Added to cart!', `${quantity} ${quantity === 1 ? 'item' : 'items'} added successfully`);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-white pt-24 pb-12 px-4">
        <div className="max-w-7xl mx-auto">
          <div className="text-center py-12">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-black"></div>
            <p className="mt-4 text-neutral-600">Loading product...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="min-h-screen bg-white pt-24 pb-12 px-4">
        <div className="max-w-7xl mx-auto">
          <div className="text-center py-12">
            <p className="text-red-600 text-lg mb-4">{error || 'Product not found'}</p>
            <Button onClick={() => router.push('/products')}>
              <ArrowLeft className="w-4 h-4 mr-2" />
              Back to Products
            </Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-white pt-24 pb-12 px-4">
      <div className="max-w-7xl mx-auto">
        {/* Back Button */}
        <Button
          variant="ghost"
          onClick={() => router.push('/products')}
          className="mb-6"
        >
          <ArrowLeft className="w-4 h-4 mr-2" />
          Back to Products
        </Button>

        {/* Product Details */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 mb-16">
          {/* Product Image */}
          <div className="relative aspect-square bg-neutral-100 rounded-lg overflow-hidden">
            <Image
              src={product.imageUrl}
              alt={product.name}
              fill
              className="object-cover"
              unoptimized
            />
          </div>

          {/* Product Info */}
          <div className="flex flex-col">
            <div className="mb-2">
              <span className="text-sm text-neutral-600 uppercase tracking-wide">
                {product.category}
              </span>
            </div>

            <h1 className="text-4xl font-bold mb-4">{product.name}</h1>

            <div className="mb-6">
              <span className="text-3xl font-bold">${product.price.toFixed(2)}</span>
            </div>

            <div className="mb-6 pb-6 border-b">
              <p className="text-neutral-700 leading-relaxed">{product.description}</p>
            </div>

            {/* Stock Status */}
            <div className="mb-6">
              {product.available && product.stockQuantity > 0 ? (
                <div className="flex items-center gap-2 text-green-600">
                  <Package className="w-5 h-5" />
                  <span className="font-medium">
                    {product.stockQuantity} in stock
                  </span>
                </div>
              ) : (
                <div className="flex items-center gap-2 text-red-600">
                  <Package className="w-5 h-5" />
                  <span className="font-medium">Out of stock</span>
                </div>
              )}
            </div>

            {/* Quantity Selector */}
            {product.available && product.stockQuantity > 0 && (
              <div className="mb-6">
                <label className="block text-sm font-medium mb-2">Quantity</label>
                <div className="flex items-center gap-3">
                  <button
                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                    className="w-10 h-10 flex items-center justify-center border border-neutral-300 rounded hover:bg-neutral-100"
                  >
                    −
                  </button>
                  <span className="text-lg font-medium w-12 text-center">{quantity}</span>
                  <button
                    onClick={() => setQuantity(Math.min(product.stockQuantity, quantity + 1))}
                    className="w-10 h-10 flex items-center justify-center border border-neutral-300 rounded hover:bg-neutral-100"
                  >
                    +
                  </button>
                </div>
              </div>
            )}

            {/* Add to Cart Button */}
            <Button
              size="lg"
              className="w-full mb-4"
              disabled={!product.available || product.stockQuantity === 0}
              onClick={addToCart}
            >
              <ShoppingCart className="w-5 h-5 mr-2" />
              Add to Cart
            </Button>

            {/* Features */}
            <Card className="mt-6">
              <CardContent className="pt-6">
                <div className="space-y-3">
                  <div className="flex items-center gap-3">
                    <Truck className="w-5 h-5 text-neutral-600" />
                    <div>
                      <p className="font-medium">Free Shipping</p>
                      <p className="text-sm text-neutral-600">On orders over $50</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    <Package className="w-5 h-5 text-neutral-600" />
                    <div>
                      <p className="font-medium">Easy Returns</p>
                      <p className="text-sm text-neutral-600">30-day return policy</p>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>

        {/* Related Products */}
        {relatedProducts.length > 0 && (
          <div className="mt-16">
            <h2 className="text-2xl font-bold mb-6">Related Products</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              {relatedProducts.map((relatedProduct) => (
                <Card
                  key={relatedProduct.id}
                  className="overflow-hidden hover:shadow-lg transition-shadow cursor-pointer"
                  onClick={() => router.push(`/products/${relatedProduct.id}`)}
                >
                  <div className="relative h-48 bg-neutral-100">
                    <Image
                      src={relatedProduct.imageUrl}
                      alt={relatedProduct.name}
                      fill
                      className="object-cover"
                      unoptimized
                    />
                  </div>
                  <CardContent className="p-4">
                    <h3 className="font-medium mb-1">{relatedProduct.name}</h3>
                    <p className="text-sm text-neutral-600 mb-2">{relatedProduct.category}</p>
                    <p className="font-bold">${relatedProduct.price.toFixed(2)}</p>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
