package ru.michaelshell.taskmanager;

import ru.michaelshell.taskmanager.model.dto.CreateTaskRequest;
import ru.michaelshell.taskmanager.model.dto.TaskDto;
import ru.michaelshell.taskmanager.model.dto.TaskStatus;
import ru.michaelshell.taskmanager.model.dto.UpdateTaskRequest;
import ru.michaelshell.taskmanager.model.entity.Task;
import ru.michaelshell.taskmanager.model.event.TaskStatusUpdatedEvent;

public class UnitTestModelFactory {
    public static final long USER_ID = 1L;
    public static final long TASK_ID = 10L;
    public static final String TASK_TITLE = "task title";
    public static final String UPDATED_TASK_TITLE = "new title";
    public static final String TASK_DESCRIPTION = "task description";
    public static final String UPDATED_TASK_DESCRIPTION = "new description";
    public static final TaskStatus TASK_STATUS = TaskStatus.TODO;
    public static final TaskStatus UPDATED_TASK_STATUS = TaskStatus.IN_PROGRESS;

    public static CreateTaskRequest getCreateTaskRequest() {
        return CreateTaskRequest.builder()
                .userId(USER_ID)
                .title(TASK_TITLE)
                .description(TASK_DESCRIPTION)
                .build();
    }

    public static Task getTask() {
        return Task.builder()
                .id(TASK_ID)
                .userId(USER_ID)
                .title(TASK_TITLE)
                .description(TASK_DESCRIPTION)
                .status(TASK_STATUS)
                .build();
    }

    public static Task getTaskWithoutId() {
        return Task.builder()
                .userId(USER_ID)
                .title(TASK_TITLE)
                .description(TASK_DESCRIPTION)
                .build();
    }

    public static UpdateTaskRequest getUpdateTaskRequest() {
        return UpdateTaskRequest.builder()
                .title(UPDATED_TASK_TITLE)
                .description(UPDATED_TASK_DESCRIPTION)
                .status(UPDATED_TASK_STATUS)
                .build();
    }

    public static TaskDto getTaskDto() {
        return TaskDto.builder()
                .id(TASK_ID)
                .userId(USER_ID)
                .title(TASK_TITLE)
                .description(TASK_DESCRIPTION)
                .status(TASK_STATUS)
                .build();
    }

    public static TaskStatusUpdatedEvent getTaskStatusUpdatedEvent() {
        return TaskStatusUpdatedEvent.builder()
                .id(TASK_ID)
                .status(UPDATED_TASK_STATUS)
                .build();
    }
}
