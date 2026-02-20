CREATE SEQUENCE message_statuses_id_seq START WITH 1 INCREMENT BY 50;

ALTER TABLE message_statuses ALTER COLUMN id SET DEFAULT nextval('message_statuses_id_seq');
