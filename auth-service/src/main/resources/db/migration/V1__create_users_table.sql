CREATE TABLE IF NOT EXISTS users (
                                     id                    VARCHAR(36)  NOT NULL PRIMARY KEY,
    email                 VARCHAR(150) NOT NULL UNIQUE,
    password_hash         TEXT         NOT NULL,
    full_name             VARCHAR(100) NOT NULL,
    status                VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    failed_login_attempts INT          NOT NULL DEFAULT 0,
    last_login_at         TIMESTAMPTZ,
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role    VARCHAR(30) NOT NULL,
    PRIMARY KEY (user_id, role)
    );

CREATE INDEX idx_users_email  ON users(email);
CREATE INDEX idx_users_status ON users(status);