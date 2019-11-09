--
-- creates country "France" with id=2
--
-- depends on: users-coder.sql
--
INSERT INTO countries(id, name, slug, created_at, created_by, updated_at, updated_by)
SELECT 2, 'France', 'france', CURRENT_TIMESTAMP(), id, CURRENT_TIMESTAMP(), id FROM users WHERE login = 'coder';
