--
-- creates a series with id=3, in Sport category and 7 stamps
--
-- depends on: users-coder.sql
-- depends on: categories-sport.sql
--
INSERT INTO series(id, quantity, perforated, category_id, created_at, created_by, updated_at, updated_by)
SELECT 3, 7, TRUE, (SELECT id FROM categories WHERE slug = 'sport'), CURRENT_TIMESTAMP(), id, CURRENT_TIMESTAMP(), id FROM users WHERE login = 'coder';
