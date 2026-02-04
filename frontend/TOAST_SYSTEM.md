# Toast Notification System - Sonner Integration

## Overview

The application uses **Sonner** for beautiful, accessible toast notifications across the entire frontend. All alerts and user feedback now use consistent, professional toast messages.

---

## Setup

### 1. Installation
```bash
npm install sonner
```

### 2. Global Configuration

The `<Toaster />` component is added to the root layout:

**File:** `app/layout.tsx`
```tsx
import { Toaster } from "sonner";

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>
        <Toaster position="top-right" richColors closeButton expand={false} />
        {children}
      </body>
    </html>
  );
}
```

---

## Usage

### Basic Toast Functions

Import the toast utility:
```tsx
import { toast } from '@/lib/toast';
```

**Success Toast:**
```tsx
toast.success('Product created!');
toast.success('Product created!', 'Changes saved successfully');
```

**Error Toast:**
```tsx
toast.error('Failed to save');
toast.error('Failed to save', 'Please check your connection');
```

**Info Toast:**
```tsx
toast.info('Logged out', 'See you next time!');
```

**Warning Toast:**
```tsx
toast.warning('Stock is low', 'Only 3 items remaining');
```

**Loading Toast:**
```tsx
const toastId = toast.loading('Processing payment...');
// Later dismiss it
toast.dismiss(toastId);
```

---

## Advanced Features

### 1. Promise Toast

Automatically handles loading, success, and error states:

```tsx
toast.promise(
  api.post('/api/products', data),
  {
    loading: 'Creating product...',
    success: 'Product created successfully!',
    error: 'Failed to create product'
  }
);
```

### 2. Action Toast

Add clickable actions to toasts:

```tsx
toast.action(
  'Item removed from cart',
  'Undo',
  () => {
    // Restore item
    addToCart(item);
  },
  'Click to undo this action'
);
```

### 3. API Toast Helper

Special helper for API responses:

```tsx
import { apiToast } from '@/lib/toast';

// Success
apiToast.success('Product saved!');

// Error with automatic parsing
try {
  await api.post('/api/products', data);
} catch (error) {
  apiToast.error(error, 'Failed to save product');
}

// Handle async operations automatically
await apiToast.handle(
  api.post('/api/products', data),
  {
    loading: 'Saving product...',
    success: 'Product saved successfully!',
    error: 'Failed to save product'
  }
);
```

---

## Current Implementation

### Files Using Toast

✅ **Authentication** (`components/AuthModal.tsx`)
- Registration success
- Login success
- Error messages

✅ **Product Details** (`app/products/[id]/page.tsx`)
- Add to cart confirmation

✅ **Admin Layout** (`app/admin/layout.tsx`)
- Access denied messages

✅ **Admin Products** (`app/admin/products/page.tsx`)
- Product created/updated
- Product deleted
- Error handling

✅ **Navigation** (`components/Navbar.tsx`)
- Logout confirmation

---

## Migration from Alert

**Before:**
```tsx
alert('Product created!');
```

**After:**
```tsx
toast.success('Product Created', 'Changes saved successfully');
```

---

## Toast Configuration

Default durations:
- Success: 3000ms (3s)
- Error: 4000ms (4s)
- Info: 3000ms (3s)
- Warning: 3500ms (3.5s)
- Action: 5000ms (5s)
- Loading: Until dismissed

Global settings:
- Position: `top-right`
- Rich colors: Enabled (colored backgrounds)
- Close button: Enabled
- Expand on hover: Disabled

---

## Best Practices

### ✅ Do

1. **Use descriptive messages:**
   ```tsx
   toast.success('Product Created', 'The product is now visible in your catalog');
   ```

2. **Include context in errors:**
   ```tsx
   apiToast.error(error, 'Failed to save product');
   ```

3. **Use promise toast for async operations:**
   ```tsx
   toast.promise(saveProduct(), {
     loading: 'Saving...',
     success: 'Saved!',
     error: 'Failed to save'
   });
   ```

4. **Provide actions when appropriate:**
   ```tsx
   toast.action('Item deleted', 'Undo', () => restore());
   ```

### ❌ Don't

1. **Don't use alerts anymore:**
   ```tsx
   alert('Success'); // ❌
   toast.success('Success'); // ✅
   ```

2. **Don't show too many toasts at once:**
   ```tsx
   // Bad
   toast.success('Item 1 saved');
   toast.success('Item 2 saved');
   toast.success('Item 3 saved');
   
   // Good
   toast.success('3 items saved successfully');
   ```

3. **Don't use console.log for user feedback:**
   ```tsx
   console.log('Product saved'); // ❌
   toast.success('Product saved'); // ✅
   ```

---

## Customization

### Custom Duration

```tsx
import { toast as sonnerToast } from 'sonner';

sonnerToast.success('Custom toast', {
  duration: 10000, // 10 seconds
});
```

### Custom Styling

```tsx
sonnerToast('Custom toast', {
  style: {
    background: 'blue',
    color: 'white',
  },
});
```

---

## Examples from Application

### Registration Success
```tsx
toast.success("Registration successful!", "Please login to continue");
```

### Login Success
```tsx
toast.success("Welcome back!", `Logged in as ${response.email}`);
```

### Add to Cart
```tsx
toast.success('Added to cart!', `${quantity} ${quantity === 1 ? 'item' : 'items'} added successfully`);
```

### Admin Access Denied
```tsx
toast.error('Access Denied', 'Admin privileges required');
```

### Product Operations
```tsx
// Create
toast.success('Product Created', 'Changes saved successfully');

// Update
toast.success('Product Updated', 'Changes saved successfully');

// Delete
toast.success('Product Deleted', 'Product removed successfully');

// Error
apiToast.error(err, 'Failed to save product');
```

---

## Testing

To test toasts in your application:

1. **Register a new user** - See success toast
2. **Login** - See welcome toast
3. **Add product to cart** - See confirmation toast
4. **Try to access admin without privileges** - See error toast
5. **Logout** - See info toast
6. **Create/edit product** - See operation toasts

---

## Performance

Sonner is highly optimized:
- **Lightweight**: ~3KB gzipped
- **Accessible**: ARIA-compliant
- **Smooth animations**: Hardware-accelerated
- **Smart positioning**: Avoids covering important content
- **Queue management**: Automatically manages multiple toasts

---

## Accessibility

Sonner toasts are fully accessible:
- Screen reader announcements
- Keyboard navigation
- Focus management
- ARIA live regions
- High contrast support

---

## Future Enhancements

Potential improvements:
1. **Persistent toasts** for critical errors
2. **Toast history** panel
3. **Custom icons** per toast type
4. **Sound notifications** (optional)
5. **Theme integration** with dark mode
6. **Analytics tracking** for error toasts

---

## Troubleshooting

### Toast not appearing?
- Check that `<Toaster />` is in root layout
- Verify import: `import { toast } from '@/lib/toast'`
- Check browser console for errors

### Toast appearing behind elements?
- Ensure `<Toaster />` has proper z-index
- Check for conflicting CSS

### Multiple toasts stacking?
- Use `toast.dismiss()` before showing new toast
- Consider using `toast.promise()` for sequential operations

---

## Summary

✅ **Consistent** - Same style across entire app
✅ **Beautiful** - Professional, modern design
✅ **Accessible** - WCAG compliant
✅ **Developer-friendly** - Simple, clean API
✅ **Production-ready** - Battle-tested library

All user feedback now flows through Sonner for a polished, professional experience! 🎉
