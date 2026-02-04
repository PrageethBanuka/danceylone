import { toast as sonnerToast } from 'sonner';

/**
 * Toast Notification Utility
 * 
 * Provides clean, consistent toast notifications across the application.
 * Built on top of Sonner for beautiful, accessible toasts.
 * 
 * Usage:
 * - toast.success('Item added to cart')
 * - toast.error('Failed to load products')
 * - toast.info('Please login to continue')
 * - toast.warning('Stock is low')
 * - toast.loading('Processing payment...')
 * - toast.promise(apiCall(), { loading: 'Saving...', success: 'Saved!', error: 'Failed' })
 */
export const toast = {
  /**
   * Success toast - for successful operations
   */
  success: (message: string, description?: string) => {
    sonnerToast.success(message, {
      description,
      duration: 3000,
    });
  },

  /**
   * Error toast - for failed operations
   */
  error: (message: string, description?: string) => {
    sonnerToast.error(message, {
      description,
      duration: 4000,
    });
  },

  /**
   * Info toast - for informational messages
   */
  info: (message: string, description?: string) => {
    sonnerToast.info(message, {
      description,
      duration: 3000,
    });
  },

  /**
   * Warning toast - for warnings
   */
  warning: (message: string, description?: string) => {
    sonnerToast.warning(message, {
      description,
      duration: 3500,
    });
  },

  /**
   * Loading toast - for ongoing operations
   * Returns toast ID to dismiss later
   */
  loading: (message: string, description?: string) => {
    return sonnerToast.loading(message, {
      description,
    });
  },

  /**
   * Dismiss a specific toast by ID
   */
  dismiss: (toastId?: string | number) => {
    sonnerToast.dismiss(toastId);
  },

  /**
   * Promise toast - automatically shows loading, success, or error based on promise
   */
  promise: <T,>(
    promise: Promise<T>,
    messages: {
      loading: string;
      success: string | ((data: T) => string);
      error: string | ((error: any) => string);
    }
  ) => {
    return sonnerToast.promise(promise, messages);
  },

  /**
   * Custom toast with action button
   */
  action: (
    message: string,
    actionLabel: string,
    actionFn: () => void,
    description?: string
  ) => {
    sonnerToast(message, {
      description,
      action: {
        label: actionLabel,
        onClick: actionFn,
      },
      duration: 5000,
    });
  },
};

/**
 * Toast helper for API responses
 */
export const apiToast = {
  /**
   * Success response handler
   */
  success: (message: string = 'Operation successful') => {
    toast.success(message);
  },

  /**
   * Error response handler with error parsing
   */
  error: (error: any, fallbackMessage: string = 'Something went wrong') => {
    const message = error?.response?.data?.message || error?.message || fallbackMessage;
    toast.error(message);
  },

  /**
   * Handle async operation with automatic toasts
   */
  async handle<T>(
    operation: Promise<T>,
    messages?: {
      loading?: string;
      success?: string;
      error?: string;
    }
  ): Promise<T> {
    const loadingToast = messages?.loading ? toast.loading(messages.loading) : null;

    try {
      const result = await operation;
      if (loadingToast) toast.dismiss(loadingToast);
      if (messages?.success) toast.success(messages.success);
      return result;
    } catch (error) {
      if (loadingToast) toast.dismiss(loadingToast);
      const errorMessage = messages?.error || 'Operation failed';
      apiToast.error(error, errorMessage);
      throw error;
    }
  },
};
