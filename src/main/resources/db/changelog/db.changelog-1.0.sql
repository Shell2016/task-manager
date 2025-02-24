--liquibase formatted sql

--changeset michaelshell:1
CREATE TABLE IF NOT EXISTS task
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    user_id     BIGINT       NOT NULL
);
