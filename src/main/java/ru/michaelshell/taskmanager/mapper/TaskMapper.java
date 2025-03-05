package ru.michaelshell.taskmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import ru.michaelshell.taskmanager.model.dto.CreateTaskRequest;
import ru.michaelshell.taskmanager.model.dto.TaskDto;
import ru.michaelshell.taskmanager.model.dto.UpdateTaskRequest;
import ru.michaelshell.taskmanager.model.entity.Task;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Task createTaskRequestToTask(CreateTaskRequest createTaskRequest);

    TaskDto taskToTaskDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void updateTask(@MappingTarget Task task, UpdateTaskRequest updateTaskRequest);
}
