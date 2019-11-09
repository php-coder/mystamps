--
-- creates a series with id=5, issued in France and having 4 stamps
--
-- depends on: users-coder.sql
-- depends on: categories-sport.sql
-- depends on: countries-france.sql
--
INSERT INTO series(id, quantity, perforated, category_id, country_id, created_at, created_by, updated_at, updated_by)
SELECT 5, 4, TRUE, (SELECT id FROM categories WHERE slug = 'sport'), (SELECT id FROM countries WHERE slug = 'france'), CURRENT_TIMESTAMP(), id, CURRENT_TIMESTAMP(), id FROM users WHERE login = 'coder';
