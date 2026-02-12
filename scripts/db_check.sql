-- Count total wallets
SELECT COUNT(*) AS wallet_count FROM wallet;

-- Count pending transactions
SELECT COUNT(*) AS pending_tx FROM wallet_transaction WHERE status='PENDING';

-- Get latest failed transactions
SELECT * FROM wallet_transaction WHERE status='FAILED' ORDER BY created_at DESC LIMIT 5;
