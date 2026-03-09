CREATE TABLE outbox_events (
                              id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              aggregate_id  VARCHAR(255) NOT NULL,
                              topic         VARCHAR(255) NOT NULL,
                              payload       BYTEA NOT NULL,
                              created_at    TIMESTAMP NOT NULL DEFAULT now(),
                              sent          BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_outbox_not_sent ON outbox_event (sent) WHERE sent = FALSE;