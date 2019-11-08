--
-- creates a series with id=1, in Fauna category and 5 stamps
--
-- depends on: users-coder.sql
-- depends on: categories-fauna.sql
--
INSERT INTO series(id, quantity, perforated, category_id, created_at, created_by, updated_at, updated_by)
SELECT 1, 5, TRUE, (SELECT id FROM categories WHERE slug = 'fauna'), CURRENT_TIMESTAMP(), id, CURRENT_TIMESTAMP(), id FROM users WHERE login = 'coder';
