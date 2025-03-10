package ru.michaelshell.taskmanager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.michaelshell.taskmanager.IntegrationTestBase;
import ru.michaelshell.taskmanager.model.entity.Task;
import ru.michaelshell.taskmanager.repository.TaskRepository;

import java.util.List;
class IntegrationTaskControllerTest extends IntegrationTestBase {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void test() {
        List<Task> tasks = taskRepository.findAll();
        System.out.println(tasks);
    }
}
