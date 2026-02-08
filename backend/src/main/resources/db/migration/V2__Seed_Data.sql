-- Seed Data for Development
-- Sample products and admin user

-- Insert admin user (password will be set by application)
-- Insert test user (password will be set by application)  
-- Note: Actual users will be created via application endpoints
-- This just seeds products

-- Insert sample products
INSERT INTO products (name, description, price, stock_quantity, category, image_url, active)
VALUES 
-- Dance Shoes
('Professional Ballet Shoes', 'High-quality canvas ballet shoes with elastic straps. Perfect for ballet classes and performances.', 45.99, 50, 'Ballet', '/images/ballet-shoes.jpg', true),
('Jazz Dance Sneakers', 'Lightweight jazz sneakers with split sole design for maximum flexibility and comfort.', 55.00, 40, 'Jazz', '/images/jazz-sneakers.jpg', true),
('Tap Shoes - Adult', 'Professional tap shoes with resonating taps for clear sound. Leather upper with cushioned insole.', 75.00, 30, 'Tap', '/images/tap-shoes.jpg', true),
('Contemporary Dance Shoes', 'Barefoot-feel dance shoes perfect for contemporary and modern dance styles.', 39.99, 45, 'Contemporary', '/images/contemporary-shoes.jpg', true),

-- Dance Apparel
('Ballet Leotard - Black', 'Classic black ballet leotard with mesh panels. Made from breathable, stretchy fabric.', 35.00, 60, 'Apparel', '/images/leotard-black.jpg', true),
('Dance Practice Skirt', 'Lightweight wrap skirt perfect for ballet and contemporary classes. Available in multiple colors.', 25.00, 55, 'Apparel', '/images/dance-skirt.jpg', true),
('Performance Tights', 'Professional-grade dance tights with exceptional durability. Convertible foot design.', 18.99, 100, 'Apparel', '/images/dance-tights.jpg', true),
('Men''s Dance Pants', 'Comfortable dance pants with stretch fabric. Ideal for rehearsals and performances.', 42.00, 35, 'Apparel', '/images/mens-pants.jpg', true),

-- Accessories
('Ballet Barre Portable', 'Adjustable portable ballet barre for home practice. Easy assembly and storage.', 125.00, 15, 'Accessories', '/images/ballet-barre.jpg', true),
('Dance Bag - Large', 'Spacious dance bag with multiple compartments for shoes, costumes, and accessories.', 45.00, 25, 'Accessories', '/images/dance-bag.jpg', true),
('Resistance Bands Set', 'Set of 5 resistance bands for dance conditioning and flexibility training.', 22.99, 50, 'Accessories', '/images/resistance-bands.jpg', true),
('Dance Floor Marley Roll', 'Professional portable dance floor (6ft x 10ft). Perfect for home studios.', 299.99, 10, 'Accessories', '/images/marley-floor.jpg', true);

