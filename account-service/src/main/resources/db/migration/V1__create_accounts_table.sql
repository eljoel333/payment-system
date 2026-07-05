CREATE TABLE IF NOT EXISTS accounts (
                                        id               VARCHAR(36)   NOT NULL PRIMARY KEY,
    user_id          VARCHAR(36)   NOT NULL,
    account_number   VARCHAR(12)   NOT NULL UNIQUE,
    type             VARCHAR(20)   NOT NULL,
    balance          NUMERIC(19,2) NOT NULL DEFAULT 0,
    currency         VARCHAR(3)    NOT NULL,
    daily_limit      NUMERIC(19,2) NOT NULL,
    daily_withdrawn  NUMERIC(19,2) NOT NULL DEFAULT 0,
    status           VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    version          BIGINT        NOT NULL DEFAULT 0
    );

CREATE INDEX idx_accounts_user_id        ON accounts(user_id);
CREATE INDEX idx_accounts_account_number ON accounts(account_number);