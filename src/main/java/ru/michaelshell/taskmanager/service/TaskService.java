package ru.michaelshell.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.michaelshell.taskmanager.mapper.TaskMapper;
import ru.michaelshell.taskmanager.model.dto.CreateTaskRequest;
import ru.michaelshell.taskmanager.model.dto.TaskDto;
import ru.michaelshell.taskmanager.repository.TaskRepository;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskDto createTask(CreateTaskRequest createTaskRequest) {
        return taskMapper.taskToTaskDto(taskRepository.save(taskMapper.createTaskRequestToTask(createTaskRequest)));
    }
}
