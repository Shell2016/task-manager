--liquibase formatted sql

--changeset michaelshell:1
INSERT INTO task (id, title, description, user_id, status)
VALUES (1, 'Task 1', 'Description for task 1', 101, 'TODO'),
       (2, 'Task 2', 'Description for task 2', 102, 'IN_PROGRESS');
SELECT SETVAL('task_id_seq', (SELECT MAX(id) FROM task));
