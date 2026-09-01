-- ============================================
-- Eliminar stripe_payment_intent_id de transactions
-- (no se usa; quitado del DTO/dominio/entidad)
-- ============================================

DROP INDEX IF EXISTS idx_transactions_stripe;

ALTER TABLE transactions
    DROP COLUMN IF EXISTS stripe_payment_intent_id;
