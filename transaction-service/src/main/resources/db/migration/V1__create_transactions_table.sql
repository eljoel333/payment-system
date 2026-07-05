CREATE TABLE IF NOT EXISTS transactions (
                                            id                VARCHAR(36)   NOT NULL PRIMARY KEY,
    type              VARCHAR(20)   NOT NULL,
    source_account_id VARCHAR(36)   NOT NULL,
    target_account_id VARCHAR(36),
    amount            NUMERIC(19,2) NOT NULL,
    currency          VARCHAR(3)    NOT NULL,
    status            VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    failure_reason    TEXT,
    correlation_id    VARCHAR(36)   NOT NULL,
    created_at        TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    completed_at      TIMESTAMPTZ
    );

CREATE INDEX idx_transactions_source      ON transactions(source_account_id);
CREATE INDEX idx_transactions_target      ON transactions(target_account_id);
CREATE INDEX idx_transactions_status      ON transactions(status);
CREATE INDEX idx_transactions_correlation ON transactions(correlation_id);