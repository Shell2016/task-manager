package ru.michaelshell.taskmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.michaelshell.taskmanager.aop.annotation.LogAfterReturning;
import ru.michaelshell.taskmanager.aop.annotation.LogAfterThrowing;
import ru.michaelshell.taskmanager.aop.annotation.LogBefore;
import ru.michaelshell.taskmanager.aop.annotation.LogExecutionTime;
import ru.michaelshell.taskmanager.model.dto.CreateTaskRequest;
import ru.michaelshell.taskmanager.model.dto.TaskDto;
import ru.michaelshell.taskmanager.model.dto.UpdateTaskRequest;
import ru.michaelshell.taskmanager.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @LogAfterReturning
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@RequestBody @Valid CreateTaskRequest createTaskRequest) {
        return taskService.createTask(createTaskRequest);
    }

    @LogExecutionTime
    @GetMapping
    public List<TaskDto> getTasks() {
        return taskService.getAllTasks();
    }

    @LogExecutionTime
    @LogAfterThrowing
    @GetMapping("/{id}")
    public TaskDto getTask(@PathVariable Long id) {
        return taskService.getTask(id);
    }

    @LogBefore
    @LogAfterReturning
    @PutMapping("/{id}")
    public TaskDto updateTask(@PathVariable Long id, @RequestBody @Valid UpdateTaskRequest updateTaskRequest) {
        return taskService.updateTask(id, updateTaskRequest);
    }

    @LogBefore
    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable Long id) {
        return taskService.deleteTask(id);
    }
}
