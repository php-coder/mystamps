UPDATE users
SET hash='@valid_user_password_hash@'
WHERE hash='@old_valid_user_password_hash@' AND role = 'USER';

UPDATE users
SET hash='@valid_admin_password_hash@'
WHERE hash='@old_valid_admin_password_hash@' AND role = 'ADMIN';
