CREATE DATABASE IF NOT EXISTS analytics;

CREATE TABLE IF NOT EXISTS analytics.auth_events
(
    event_type  LowCardinality(String),
    user_id     UUID                   DEFAULT toUUID('00000000-0000-0000-0000-000000000000'),
    ip_address  String                 DEFAULT '',
    user_agent  String                 DEFAULT '',
    country     LowCardinality(String) DEFAULT '',
    occurred_at DateTime64(3, 'UTC'),
    created_at  DateTime               DEFAULT now(),

    INDEX idx_user_id  user_id    TYPE bloom_filter(0.01) GRANULARITY 4,
    INDEX idx_ip       ip_address TYPE bloom_filter(0.01) GRANULARITY 4,
    INDEX idx_country  country    TYPE set(50)            GRANULARITY 4
)
ENGINE = MergeTree()
PARTITION BY toYYYYMM(occurred_at)
ORDER BY (event_type, occurred_at);

CREATE TABLE IF NOT EXISTS analytics.message_events
(
    message_id  Int64,
    user_id     UUID                   DEFAULT
                                           toUUID('00000000-0000-0000-0000-000000000000'),
    chat_id     UUID                   DEFAULT
                                           toUUID('00000000-0000-0000-0000-000000000000'),
    event_type  LowCardinality(String),
    ip_address  String                 DEFAULT '',
    user_agent  String                 DEFAULT '',
    char_number Int64                  DEFAULT 0,
    have_file   Bool                   DEFAULT false,
    country     LowCardinality(String) DEFAULT '',
    occurred_at DateTime64(3, 'UTC'),
    created_at  DateTime               DEFAULT now(),

    INDEX idx_user_id  user_id  TYPE bloom_filter(0.01) GRANULARITY 4,
    INDEX idx_chat_id  chat_id  TYPE bloom_filter(0.01) GRANULARITY 4,
    INDEX idx_country  country  TYPE set(50)            GRANULARITY 4
)
    ENGINE = MergeTree()
        PARTITION BY toYYYYMM(occurred_at)
        ORDER BY (event_type, occurred_at);