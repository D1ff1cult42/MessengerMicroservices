CREATE TABLE mail_tokens
(
    id           BIGSERIAL PRIMARY KEY,
    token        UUID         NOT NULL UNIQUE,
    user_id      UUID         NOT NULL,
    email        VARCHAR(255) NOT NULL,
    is_used      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    expires_at   TIMESTAMP    NOT NULL,
    confirmed_at TIMESTAMP
);

CREATE INDEX idx_mail_tokens_token ON mail_tokens (token);
CREATE INDEX idx_mail_tokens_user_id ON mail_tokens (user_id);
