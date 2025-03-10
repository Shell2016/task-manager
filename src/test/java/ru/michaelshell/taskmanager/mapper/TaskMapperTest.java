package ru.michaelshell.taskmanager.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.michaelshell.taskmanager.model.dto.TaskDto;
import ru.michaelshell.taskmanager.model.entity.Task;

import static ru.michaelshell.taskmanager.TestModelFactory.TASK_DESCRIPTION;
import static ru.michaelshell.taskmanager.TestModelFactory.TASK_ID;
import static ru.michaelshell.taskmanager.TestModelFactory.TASK_STATUS;
import static ru.michaelshell.taskmanager.TestModelFactory.TASK_TITLE;
import static ru.michaelshell.taskmanager.TestModelFactory.UPDATED_TASK_DESCRIPTION;
import static ru.michaelshell.taskmanager.TestModelFactory.UPDATED_TASK_STATUS;
import static ru.michaelshell.taskmanager.TestModelFactory.UPDATED_TASK_TITLE;
import static ru.michaelshell.taskmanager.TestModelFactory.USER_ID;
import static ru.michaelshell.taskmanager.TestModelFactory.getCreateTaskRequest;
import static ru.michaelshell.taskmanager.TestModelFactory.getTask;
import static ru.michaelshell.taskmanager.TestModelFactory.getUpdateTaskRequest;


@ExtendWith({SoftAssertionsExtension.class, MockitoExtension.class})
class TaskMapperTest {
    private TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);

    @Test
    void createTaskRequestToTask(SoftAssertions softly) {
        Task task = taskMapper.createTaskRequestToTask(getCreateTaskRequest());

        softly.assertThat(task).isNotNull();
        softly.assertThat(task.getId()).isNull();
        softly.assertThat(task.getUserId()).isEqualTo(USER_ID);
        softly.assertThat(task.getTitle()).isEqualTo(TASK_TITLE);
        softly.assertThat(task.getDescription()).isEqualTo(TASK_DESCRIPTION);
        softly.assertThat(task.getStatus()).isNull();
    }

    @Test
    void taskToTaskDto(SoftAssertions softly) {
        TaskDto taskDto = taskMapper.taskToTaskDto(getTask());

        softly.assertThat(taskDto).isNotNull();
        softly.assertThat(taskDto.id()).isEqualTo(TASK_ID);
        softly.assertThat(taskDto.title()).isEqualTo(TASK_TITLE);
        softly.assertThat(taskDto.description()).isEqualTo(TASK_DESCRIPTION);
        softly.assertThat(taskDto.userId()).isEqualTo(USER_ID);
        softly.assertThat(taskDto.status()).isEqualTo(TASK_STATUS);
    }

    @Test
    void updateTask(SoftAssertions softly) {
        Task task = getTask();
        taskMapper.updateTask(task, getUpdateTaskRequest());

        softly.assertThat(task).isNotNull();
        softly.assertThat(task.getId()).isEqualTo(TASK_ID);
        softly.assertThat(task.getTitle()).isEqualTo(UPDATED_TASK_TITLE);
        softly.assertThat(task.getDescription()).isEqualTo(UPDATED_TASK_DESCRIPTION);
        softly.assertThat(task.getUserId()).isEqualTo(USER_ID);
        softly.assertThat(task.getStatus()).isEqualTo(UPDATED_TASK_STATUS);
    }
}