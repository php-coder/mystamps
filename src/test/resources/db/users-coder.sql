--
-- creates user "coder" with id=1
--
INSERT INTO users(id, login, role, name, email, hash, registered_at, activated_at)
VALUES(1, 'coder', 'USER', 'Coder', 'coder@example.com', '<password-hash>', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
