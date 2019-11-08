--
-- creates category "Fauna" with id=2 and name in Russian
--
-- depends on: users-coder.sql
--
INSERT INTO categories(id, name, name_ru, slug, created_at, created_by, updated_at, updated_by)
SELECT 2, 'Fauna', 'Фауна', 'fauna', CURRENT_TIMESTAMP(), id, CURRENT_TIMESTAMP(), id FROM users WHERE login = 'coder';
