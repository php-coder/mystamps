--
-- creates collection with id=1 for user "coder"
--
-- depends on: users-coder.sql
--
INSERT INTO collections(id, user_id, slug, updated_at, updated_by)
SELECT 1, id, 'coder', CURRENT_TIMESTAMP(), id FROM users WHERE login = 'coder';
