--liquibase formatted sql

--changeset michaelshell:1
INSERT INTO task (title, description, user_id, status)
VALUES ('Task 1', 'Description for task 1', 101, 'TODO'),
       ('Task 2', 'Description for task 2', 102, 'IN_PROGRESS'),
       ('Task 3', 'Description for task 3', 103, 'DONE'),
       ('Task 4', 'Description for task 4', 104, 'TODO'),
       ('Task 5', 'Description for task 5', 105, 'REVIEW');