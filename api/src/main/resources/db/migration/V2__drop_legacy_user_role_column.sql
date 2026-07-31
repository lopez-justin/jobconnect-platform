-- Quitar índice viejo
DROP INDEX IF EXISTS idx_users_role;

-- Quitar columna legacy
ALTER TABLE users DROP COLUMN IF EXISTS role;