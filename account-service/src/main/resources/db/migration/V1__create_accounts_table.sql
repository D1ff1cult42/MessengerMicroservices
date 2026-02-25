CREATE TABLE accounts
(
    user_id            UUID         NOT NULL PRIMARY KEY,
    email              VARCHAR(255) NOT NULL UNIQUE,
    username           VARCHAR(255),
    avatar_object_name VARCHAR(255),
    avatar_bucket_name VARCHAR(255),
    description        TEXT,
    created_at         TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at         TIMESTAMP,
    is_verified        BOOLEAN      NOT NULL DEFAULT FALSE
);
