ALTER TABLE sys_user
    ADD COLUMN account_level VARCHAR(32) NOT NULL DEFAULT 'STAFF',
    ADD COLUMN manager_user_id BIGINT NULL;
