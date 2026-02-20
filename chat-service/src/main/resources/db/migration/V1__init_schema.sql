CREATE TABLE IF NOT EXISTS chats (
    id UUID PRIMARY KEY,
    created_by UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    icon_object_name VARCHAR(255),
    icon_bucket_name VARCHAR(255),
    icon_file_size BIGINT,
    icon_updated_at TIMESTAMP,
    description TEXT,
    is_group_chat BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chat_participants (
    id UUID PRIMARY KEY,
    chat_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat FOREIGN KEY (chat_id) REFERENCES chats(id) ON DELETE CASCADE,
    CONSTRAINT uq_chat_user UNIQUE (chat_id, user_id)
);

CREATE INDEX idx_chat_participants_chat_id ON chat_participants(chat_id);

CREATE INDEX idx_chat_participants_user_id ON chat_participants(user_id);

