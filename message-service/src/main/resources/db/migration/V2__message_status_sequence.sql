ALTER TABLE message_statuses ALTER COLUMN id DROP IDENTITY IF EXISTS;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE c.relkind = 'S' AND c.relname = 'message_statuses_id_seq'
    ) THEN
        CREATE SEQUENCE message_statuses_id_seq START WITH 1 INCREMENT BY 50;
    END IF;
END
$$;

ALTER TABLE message_statuses ALTER COLUMN id SET DEFAULT nextval('message_statuses_id_seq');
