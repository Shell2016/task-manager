--liquibase formatted sql

--changeset michaelshell:1
ALTER TABLE task
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'TODO';