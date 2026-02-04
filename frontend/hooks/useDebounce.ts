import { useEffect, useState } from 'react';

/**
 * useDebounce Hook
 * 
 * LEARNING POINT FOR INTERNSHIP:
 * Debouncing prevents excessive API calls by waiting for user to stop typing.
 * 
 * Example: User types "shoes"
 * - Without debounce: 5 API calls (s, sh, sho, shoe, shoes)
 * - With debounce: 1 API call (after user stops typing)
 * 
 * This improves:
 * - Performance (fewer network requests)
 * - Server load (less database queries)
 * - User experience (less flickering)
 * 
 * @param value - The value to debounce
 * @param delay - Delay in milliseconds (typically 300-500ms)
 * @returns Debounced value
 */
export function useDebounce<T>(value: T, delay: number = 500): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    // Set up a timer to update the debounced value after delay
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    // Clean up: cancel the timer if value changes before delay expires
    // This is called every time value changes or component unmounts
    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]); // Re-run effect when value or delay changes

  return debouncedValue;
}
