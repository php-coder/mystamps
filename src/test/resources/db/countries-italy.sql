--
-- creates country "Italy" with id=1 and name in Russian
--
-- depends on: users-coder.sql
--
INSERT INTO countries(id, name, name_ru, slug, created_at, created_by, updated_at, updated_by)
SELECT 1, 'Italy', 'Италия', 'italy', CURRENT_TIMESTAMP(), id, CURRENT_TIMESTAMP(), id FROM users WHERE login = 'coder';
