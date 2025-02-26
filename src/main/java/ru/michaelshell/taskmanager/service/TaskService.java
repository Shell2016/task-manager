package ru.michaelshell.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.michaelshell.taskmanager.exception.ResourceNotFoundException;
import ru.michaelshell.taskmanager.mapper.TaskMapper;
import ru.michaelshell.taskmanager.model.dto.CreateTaskRequest;
import ru.michaelshell.taskmanager.model.dto.TaskDto;
import ru.michaelshell.taskmanager.model.dto.TaskStatus;
import ru.michaelshell.taskmanager.model.dto.UpdateTaskRequest;
import ru.michaelshell.taskmanager.model.entity.Task;
import ru.michaelshell.taskmanager.model.event.TaskStatusUpdatedEvent;
import ru.michaelshell.taskmanager.repository.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final KafkaTemplate<String, TaskStatusUpdatedEvent> kafkaTemplate;

    @Transactional
    public TaskDto createTask(CreateTaskRequest createTaskRequest) {
        return taskMapper.taskToTaskDto(taskRepository.save(taskMapper.createTaskRequestToTask(createTaskRequest)));
    }

    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::taskToTaskDto)
                .toList();
    }

    public TaskDto getTask(Long id) {
        return taskRepository.findById(id)
                .map(taskMapper::taskToTaskDto)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " was not found!"));
    }

    @Transactional
    public TaskDto updateTask(Long id, UpdateTaskRequest updateTaskRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " was not found!"));
        TaskStatus oldStatus = task.getStatus();
        taskMapper.updateTask(task, updateTaskRequest);

        if (oldStatus != task.getStatus()) {
            kafkaTemplate.send("task-status-updated-events-topic",
                    id.toString(),
                    TaskStatusUpdatedEvent.builder()
                            .id(task.getId())
                            .status(task.getStatus())
                            .build());
        }
        return taskMapper.taskToTaskDto(task);
    }

    @Transactional
    public String deleteTask(Long id) {
        return taskRepository.deleteTaskById(id) == 1 ? "Task has been deleted." : "Task was not found.";
    }


}
