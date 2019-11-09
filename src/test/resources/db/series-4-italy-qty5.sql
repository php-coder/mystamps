--
-- creates a series with id=4, issued in Italy and having 5 stamps
--
-- depends on: users-coder.sql
-- depends on: categories-sport.sql
-- depends on: countries-italy.sql
--
INSERT INTO series(id, quantity, perforated, category_id, country_id, created_at, created_by, updated_at, updated_by)
SELECT 4, 5, TRUE, (SELECT id FROM categories WHERE slug = 'sport'), (SELECT id FROM countries WHERE slug = 'italy'), CURRENT_TIMESTAMP(), id, CURRENT_TIMESTAMP(), id FROM users WHERE login = 'coder';
