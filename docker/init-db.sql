SELECT 'CREATE DATABASE payment_accounts' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'payment_accounts')\gexec
SELECT 'CREATE DATABASE payment_transactions' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'payment_transactions')\gexec
