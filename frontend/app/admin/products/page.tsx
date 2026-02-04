'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import { toast, apiToast } from '@/lib/toast';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Plus, Edit, Trash2, Package } from 'lucide-react';
import Image from 'next/image';

/**
 * Admin Product Management Page
 * 
 * LEARNING POINTS FOR INTERNSHIP:
 * 
 * 1. CRUD OPERATIONS
 *    - Create, Read, Update, Delete products
 *    - RESTful API integration
 *    - Form validation and error handling
 * 
 * 2. MODAL PATTERNS
 *    - Reusable dialog for create/edit
 *    - Controlled form inputs
 *    - Optimistic UI updates
 * 
 * 3. STATE MANAGEMENT
 *    - Local state for products list
 *    - Form state for create/edit
 *    - Loading/error states
 * 
 * 4. ADMIN UX
 *    - Inline editing
 *    - Confirmation dialogs
 *    - Immediate feedback
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

interface ProductFormData {
  name: string;
  description: string;
  price: string;
  category: string;
  imageUrl: string;
  stockQuantity: string;
}

export default function AdminProductsPage() {
  const router = useRouter();
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // Modal state
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  
  // Form state
  const [formData, setFormData] = useState<ProductFormData>({
    name: '',
    description: '',
    price: '',
    category: 'Shoes',
    imageUrl: '',
    stockQuantity: ''
  });

  const [formErrors, setFormErrors] = useState<Partial<ProductFormData>>({});

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const response = await api.get('/api/products');
      setProducts(response.data);
      setError('');
    } catch (err) {
      setError('Failed to load products');
      console.error('Error fetching products:', err);
    } finally {
      setLoading(false);
    }
  };

  const openCreateModal = () => {
    setEditingProduct(null);
    setFormData({
      name: '',
      description: '',
      price: '',
      category: 'Shoes',
      imageUrl: '',
      stockQuantity: ''
    });
    setFormErrors({});
    setIsModalOpen(true);
  };

  const openEditModal = (product: Product) => {
    setEditingProduct(product);
    setFormData({
      name: product.name,
      description: product.description,
      price: product.price.toString(),
      category: product.category,
      imageUrl: product.imageUrl,
      stockQuantity: product.stockQuantity.toString()
    });
    setFormErrors({});
    setIsModalOpen(true);
  };

  const validateForm = (): boolean => {
    const errors: Partial<ProductFormData> = {};

    if (!formData.name.trim()) errors.name = 'Name is required';
    if (!formData.description.trim()) errors.description = 'Description is required';
    if (!formData.price || parseFloat(formData.price) <= 0) errors.price = 'Valid price required';
    if (!formData.imageUrl.trim()) errors.imageUrl = 'Image URL is required';
    if (!formData.stockQuantity || parseInt(formData.stockQuantity) < 0) errors.stockQuantity = 'Valid stock quantity required';

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) return;

    try {
      const productData = {
        name: formData.name,
        description: formData.description,
        price: parseFloat(formData.price),
        category: formData.category,
        imageUrl: formData.imageUrl,
        stockQuantity: parseInt(formData.stockQuantity)
      };

      if (editingProduct) {
        // Update existing product
        await api.put(`/api/products/${editingProduct.id}`, productData);
      } else {
        // Create new product
        await api.post('/api/products', productData);
      }

      setIsModalOpen(false);
      fetchProducts();
      toast.success(editingProduct ? 'Product Updated' : 'Product Created', 'Changes saved successfully');
    } catch (err: any) {
      console.error('Error saving product:', err);
      apiToast.error(err, 'Failed to save product');
    }
  };

  const handleDelete = async (productId: string) => {
    if (!confirm('Are you sure you want to delete this product?')) return;

    try {
      await api.delete(`/api/products/${productId}`);
      fetchProducts();
      toast.success('Product Deleted', 'Product removed successfully');
    } catch (err) {
      console.error('Error deleting product:', err);
      toast.error('Failed to delete product');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold">Product Management</h2>
          <p className="text-neutral-600 mt-1">Manage your product catalog</p>
        </div>
        <Button onClick={openCreateModal} className="flex items-center gap-2">
          <Plus className="h-4 w-4" />
          Add Product
        </Button>
      </div>

      {/* Error State */}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded">
          {error}
        </div>
      )}

      {/* Loading State */}
      {loading ? (
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-black"></div>
          <p className="mt-4 text-neutral-600">Loading products...</p>
        </div>
      ) : (
        <>
          {/* Products List */}
          {products.length === 0 ? (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <Package className="h-16 w-16 text-neutral-400 mb-4" />
                <p className="text-neutral-600 text-lg font-medium">No products yet</p>
                <p className="text-neutral-500 mt-2">Start by adding your first product</p>
                <Button onClick={openCreateModal} className="mt-4">
                  <Plus className="h-4 w-4 mr-2" />
                  Add Product
                </Button>
              </CardContent>
            </Card>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {products.map((product) => (
                <Card key={product.id} className="overflow-hidden">
                  {/* Product Image */}
                  <div className="relative h-48 bg-neutral-100">
                    <Image
                      src={product.imageUrl}
                      alt={product.name}
                      fill
                      className="object-cover"
                      unoptimized
                    />
                    <div className={`absolute top-2 right-2 px-2 py-1 rounded text-xs font-medium ${
                      product.available ? 'bg-green-500 text-white' : 'bg-red-500 text-white'
                    }`}>
                      {product.available ? 'Active' : 'Inactive'}
                    </div>
                  </div>

                  <CardHeader>
                    <div className="flex justify-between items-start">
                      <CardTitle className="text-lg">{product.name}</CardTitle>
                      <span className="text-xl font-bold">${product.price}</span>
                    </div>
                    <CardDescription>{product.category}</CardDescription>
                  </CardHeader>

                  <CardContent className="space-y-4">
                    <p className="text-sm text-neutral-700 line-clamp-2">
                      {product.description}
                    </p>
                    <p className="text-xs text-neutral-500">
                      Stock: {product.stockQuantity} units
                    </p>

                    <div className="flex gap-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => openEditModal(product)}
                        className="flex-1"
                      >
                        <Edit className="h-4 w-4 mr-1" />
                        Edit
                      </Button>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleDelete(product.id)}
                        className="flex-1 text-red-600 hover:text-red-700 hover:bg-red-50"
                      >
                        <Trash2 className="h-4 w-4 mr-1" />
                        Delete
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </>
      )}

      {/* Create/Edit Product Modal */}
      <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
        <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>
              {editingProduct ? 'Edit Product' : 'Add New Product'}
            </DialogTitle>
            <DialogDescription>
              {editingProduct 
                ? 'Update the product details below' 
                : 'Fill in the details to add a new product to your catalog'}
            </DialogDescription>
          </DialogHeader>

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Name */}
            <div>
              <Label htmlFor="name">Product Name *</Label>
              <Input
                id="name"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                placeholder="Premium Dance Shoes"
              />
              {formErrors.name && (
                <p className="text-red-600 text-sm mt-1">{formErrors.name}</p>
              )}
            </div>

            {/* Description */}
            <div>
              <Label htmlFor="description">Description *</Label>
              <textarea
                id="description"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="High-quality dance shoes designed for professionals..."
                className="w-full min-h-[100px] px-3 py-2 border rounded-md"
              />
              {formErrors.description && (
                <p className="text-red-600 text-sm mt-1">{formErrors.description}</p>
              )}
            </div>

            {/* Price and Stock in Row */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="price">Price ($) *</Label>
                <Input
                  id="price"
                  type="number"
                  step="0.01"
                  value={formData.price}
                  onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                  placeholder="89.99"
                />
                {formErrors.price && (
                  <p className="text-red-600 text-sm mt-1">{formErrors.price}</p>
                )}
              </div>

              <div>
                <Label htmlFor="stockQuantity">Stock Quantity *</Label>
                <Input
                  id="stockQuantity"
                  type="number"
                  value={formData.stockQuantity}
                  onChange={(e) => setFormData({ ...formData, stockQuantity: e.target.value })}
                  placeholder="50"
                />
                {formErrors.stockQuantity && (
                  <p className="text-red-600 text-sm mt-1">{formErrors.stockQuantity}</p>
                )}
              </div>
            </div>

            {/* Category */}
            <div>
              <Label htmlFor="category">Category *</Label>
              <select
                id="category"
                value={formData.category}
                onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                className="w-full px-3 py-2 border rounded-md"
              >
                <option value="Shoes">Shoes</option>
                <option value="Dancewear">Dancewear</option>
                <option value="Accessories">Accessories</option>
              </select>
            </div>

            {/* Image URL */}
            <div>
              <Label htmlFor="imageUrl">Image URL *</Label>
              <Input
                id="imageUrl"
                value={formData.imageUrl}
                onChange={(e) => setFormData({ ...formData, imageUrl: e.target.value })}
                placeholder="https://example.com/image.jpg"
              />
              {formErrors.imageUrl && (
                <p className="text-red-600 text-sm mt-1">{formErrors.imageUrl}</p>
              )}
              {formData.imageUrl && (
                <div className="mt-2 relative h-32 bg-neutral-100 rounded">
                  <Image
                    src={formData.imageUrl}
                    alt="Preview"
                    fill
                    className="object-cover rounded"
                    unoptimized
                  />
                </div>
              )}
            </div>

            {/* Actions */}
            <div className="flex justify-end gap-2 pt-4">
              <Button
                type="button"
                variant="outline"
                onClick={() => setIsModalOpen(false)}
              >
                Cancel
              </Button>
              <Button type="submit">
                {editingProduct ? 'Update Product' : 'Create Product'}
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
