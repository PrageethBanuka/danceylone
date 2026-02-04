'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useDebounce } from '@/hooks/useDebounce';
import { Search, X } from 'lucide-react';
import Image from 'next/image';

/**
 * Product Catalog Page
 * 
 * LEARNING POINTS FOR INTERNSHIP:
 * 
 * 1. CLIENT-SIDE DATA FETCHING
 *    - useEffect with empty deps [] = fetch on mount
 *    - useState for loading, error, data states
 *    - This is the "traditional" React pattern
 * 
 * 2. DEBOUNCING FOR PERFORMANCE
 *    - useDebounce hook prevents excessive API calls
 *    - Waits for user to stop typing before searching
 *    - Critical for production apps with real users
 * 
 * 3. ERROR HANDLING
 *    - Try-catch around API calls
 *    - User-friendly error messages
 *    - Loading states for UX
 * 
 * 4. COMPONENT COMPOSITION
 *    - Break down into smaller components
 *    - Use shadcn/ui for consistent design
 *    - Reusable ProductCard component
 * 
 * NEXT STEPS TO LEARN:
 * - Server Components (fetch on server, faster)
 * - Suspense boundaries
 * - React Query for caching
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

export default function ProductsPage() {
  const router = useRouter();
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  
  // Debounce search term to avoid API calls on every keystroke
  const debouncedSearchTerm = useDebounce(searchTerm, 400);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      setError('');
      
      // Build query params
      let url = '/api/products';
      const params = new URLSearchParams();
      if (debouncedSearchTerm) params.append('search', debouncedSearchTerm);
      if (selectedCategory) params.append('category', selectedCategory);
      if (params.toString()) url += `?${params.toString()}`;

      const response = await api.get(url);
      setProducts(response.data);
    } catch (err) {
      setError('Failed to load products. Please try again.');
      console.error('Error fetching products:', err);
    } finally {
      setLoading(false);
    }
  };

  // Re-fetch when debounced search or filter changes
  useEffect(() => {
    fetchProducts();
  }, [debouncedSearchTerm, selectedCategory]);

  const categories = ['All', 'Shoes', 'Dancewear', 'Accessories'];

  const clearFilters = () => {
    setSearchTerm('');
    setSelectedCategory('');
  };

  const hasActiveFilters = searchTerm || selectedCategory;

  const addToCart = (product: Product) => {
    const cart = JSON.parse(localStorage.getItem('cart') || '[]');
    const existing = cart.find((item: any) => item.id === product.id);
    
    if (existing) {
      existing.quantity += 1;
    } else {
      cart.push({
        id: product.id,
        name: product.name,
        price: product.price,
        quantity: 1,
        imageUrl: product.imageUrl
      });
    }
    
    localStorage.setItem('cart', JSON.stringify(cart));
    
    // Dispatch event to update navbar badge
    window.dispatchEvent(new Event('cartUpdated'));
  };

  return (
    <div className="min-h-screen bg-white pt-24 pb-12 px-4">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold mb-2">Product Catalog</h1>
          <p className="text-neutral-600">Discover our premium dance collection</p>
        </div>

        {/* Search and Filter */}
        <div className="mb-8 space-y-4">
          {/* Search Bar */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-neutral-400 h-5 w-5" />
            <Input
              type="text"
              placeholder="Search products..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10 pr-10"
            />
            {searchTerm && (
              <button
                onClick={() => setSearchTerm('')}
                className="absolute right-3 top-1/2 transform -translate-y-1/2 text-neutral-400 hover:text-neutral-600"
              >
                <X className="h-5 w-5" />
              </button>
            )}
          </div>

          {/* Category Filters */}
          <div className="flex flex-wrap gap-2 items-center">
            <span className="text-sm text-neutral-600 font-medium">Categories:</span>
            {categories.map((cat) => (
              <Button
                key={cat}
                variant={selectedCategory === (cat === 'All' ? '' : cat) ? 'default' : 'outline'}
                onClick={() => setSelectedCategory(cat === 'All' ? '' : cat)}
                size="sm"
                className="rounded-full"
              >
                {cat}
              </Button>
            ))}
            
            {/* Clear Filters Button */}
            {hasActiveFilters && (
              <Button
                variant="ghost"
                onClick={clearFilters}
                size="sm"
                className="text-neutral-500 hover:text-neutral-700"
              >
                <X className="h-4 w-4 mr-1" />
                Clear filters
              </Button>
            )}
          </div>

          {/* Active Filters Display */}
          {hasActiveFilters && (
            <div className="flex flex-wrap gap-2 items-center pt-2 border-t">
              <span className="text-sm text-neutral-500">Active filters:</span>
              {searchTerm && (
                <div className="bg-black text-white text-sm px-3 py-1 rounded-full flex items-center gap-2">
                  Search: "{searchTerm}"
                  <button
                    onClick={() => setSearchTerm('')}
                    className="hover:bg-neutral-800 rounded-full p-0.5"
                  >
                    <X className="h-3 w-3" />
                  </button>
                </div>
              )}
              {selectedCategory && (
                <div className="bg-black text-white text-sm px-3 py-1 rounded-full flex items-center gap-2">
                  Category: {selectedCategory}
                  <button
                    onClick={() => setSelectedCategory('')}
                    className="hover:bg-neutral-800 rounded-full p-0.5"
                  >
                    <X className="h-3 w-3" />
                  </button>
                </div>
              )}
            </div>
          )}
        </div>

        {/* Loading State with Skeleton */}
        {loading && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {[...Array(8)].map((_, i) => (
              <div key={i} className="border rounded-lg overflow-hidden animate-pulse">
                <div className="h-48 bg-neutral-200"></div>
                <div className="p-6 space-y-3">
                  <div className="h-6 bg-neutral-200 rounded w-3/4"></div>
                  <div className="h-4 bg-neutral-200 rounded w-1/2"></div>
                  <div className="h-4 bg-neutral-200 rounded"></div>
                  <div className="h-4 bg-neutral-200 rounded w-5/6"></div>
                  <div className="h-10 bg-neutral-200 rounded mt-4"></div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Error State */}
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded">
            {error}
          </div>
        )}

        {/* Products Grid */}
        {!loading && !error && (
          <>
            {products.length === 0 ? (
              <div className="text-center py-12">
                <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-neutral-100 mb-4">
                  <Search className="h-8 w-8 text-neutral-400" />
                </div>
                <p className="text-neutral-600 text-lg font-medium">No products found</p>
                <p className="text-neutral-500 mt-2">Try adjusting your search or filters</p>
                {hasActiveFilters && (
                  <Button 
                    onClick={clearFilters}
                    variant="outline"
                    className="mt-4"
                  >
                    Clear all filters
                  </Button>
                )}
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                {products.map((product) => (
                  <ProductCard 
                    key={product.id} 
                    product={product} 
                    onAddToCart={addToCart}
                    onClick={() => router.push(`/products/${product.id}`)}
                  />
                ))}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}

/**
 * Product Card Component
 * 
 * DESIGN PATTERN: Extract reusable components
 * Makes code cleaner and components testable
 */
function ProductCard({ 
  product, 
  onAddToCart, 
  onClick 
}: { 
  product: Product; 
  onAddToCart: (product: Product) => void;
  onClick: () => void;
}) {
  const handleAddToCart = (e: React.MouseEvent) => {
    e.stopPropagation(); // Prevent card click when clicking button
    onAddToCart(product);
  };

  return (
    <Card 
      className="overflow-hidden hover:shadow-lg transition-shadow flex flex-col h-full cursor-pointer"
      onClick={onClick}
    >
      {/* Product Image */}
      <div className="relative h-48 bg-neutral-100 flex-shrink-0">
        <Image
          src={product.imageUrl}
          alt={product.name}
          fill
          className="object-cover"
          unoptimized // For external URLs
        />
      </div>

      <CardHeader className="flex-shrink-0">
        <div className="flex justify-between items-start gap-3">
          <CardTitle className="text-lg flex-1">{product.name}</CardTitle>
          <span className="text-xl font-bold bg-neutral-100 px-3 py-1 rounded-lg whitespace-nowrap">
            ${product.price}
          </span>
        </div>
        <CardDescription className="text-sm text-neutral-600 mt-2">
          {product.category}
        </CardDescription>
      </CardHeader>

      <CardContent className="flex-grow">
        <p className="text-sm text-neutral-700 line-clamp-2">
          {product.description}
        </p>
        <p className="text-xs text-neutral-500 mt-2">
          {product.stockQuantity > 0 
            ? `${product.stockQuantity} in stock` 
            : 'Out of stock'}
        </p>
      </CardContent>

      <CardFooter className="flex-shrink-0 mt-auto">
        <Button 
          className="w-full" 
          disabled={!product.available || product.stockQuantity === 0}
          onClick={handleAddToCart}
        >
          {product.available && product.stockQuantity > 0 
            ? 'Add to Cart' 
            : 'Out of Stock'}
        </Button>
      </CardFooter>
    </Card>
  );
}
