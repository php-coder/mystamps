--
-- creates category "Sport" with id=1
--
-- depends on: users-coder.sql
--
INSERT INTO categories(id, name, slug, created_at, created_by, updated_at, updated_by)
SELECT 1, 'Sport', 'sport', CURRENT_TIMESTAMP(), id, CURRENT_TIMESTAMP(), id FROM users WHERE login = 'coder';
