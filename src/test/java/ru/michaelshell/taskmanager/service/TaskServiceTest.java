package ru.michaelshell.taskmanager.service;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.michaelshell.taskmanager.TestModelFactory;
import ru.michaelshell.taskmanager.exception.ResourceNotFoundException;
import ru.michaelshell.taskmanager.kafka.producer.KafkaProducer;
import ru.michaelshell.taskmanager.mapper.TaskMapper;
import ru.michaelshell.taskmanager.model.dto.CreateTaskRequest;
import ru.michaelshell.taskmanager.model.dto.TaskDto;
import ru.michaelshell.taskmanager.model.dto.UpdateTaskRequest;
import ru.michaelshell.taskmanager.model.entity.Task;
import ru.michaelshell.taskmanager.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ru.michaelshell.taskmanager.TestModelFactory.TASK_ID;
import static ru.michaelshell.taskmanager.TestModelFactory.UPDATED_TASK_STATUS;
import static ru.michaelshell.taskmanager.TestModelFactory.getCreateTaskRequest;
import static ru.michaelshell.taskmanager.TestModelFactory.getTaskDto;
import static ru.michaelshell.taskmanager.TestModelFactory.getTaskStatusUpdatedEvent;
import static ru.michaelshell.taskmanager.TestModelFactory.getUpdateTaskRequest;

@ExtendWith({SoftAssertionsExtension.class, MockitoExtension.class})
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private KafkaProducer kafkaProducer;
    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask() {
        CreateTaskRequest createTaskRequest = getCreateTaskRequest();
        Task taskBeforeSaving = TestModelFactory.getTaskWithoutId();
        Task savedTask = TestModelFactory.getTask();
        TaskDto taskDto = getTaskDto();
        when(taskMapper.createTaskRequestToTask(createTaskRequest)).thenReturn(taskBeforeSaving);
        when(taskRepository.save(taskBeforeSaving)).thenReturn(savedTask);
        when(taskMapper.taskToTaskDto(savedTask)).thenReturn(taskDto);

        TaskDto result = taskService.createTask(createTaskRequest);

        assertThat(result).isEqualTo(taskDto);
        verify(taskMapper).createTaskRequestToTask(any());
        verify(taskMapper).taskToTaskDto(any());
        verify(taskRepository).save(any());
    }

    @Test
    void getAllTasks() {
        Task task = TestModelFactory.getTask();
        TaskDto taskDto = getTaskDto();
        when(taskRepository.findAll()).thenReturn(List.of(task, task));
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);

        List<TaskDto> result = taskService.getAllTasks();

        assertThat(result)
                .hasSize(2)
                .containsExactly(taskDto, taskDto);
        verify(taskRepository).findAll();
        verify(taskMapper, times(2)).taskToTaskDto(any());
    }

    @Test
    void getTask() {
        Task task = TestModelFactory.getTask();
        TaskDto taskDto = getTaskDto();
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.getTask(TASK_ID);
        assertThat(result).isEqualTo(taskDto);
        verify(taskRepository).findById(any());
        verify(taskMapper).taskToTaskDto(any());
    }

    @Test
    void getTask_IfTaskNotFound_ShouldThrowException() {
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTask(TASK_ID));
        verify(taskRepository).findById(any());
        verifyNoInteractions(taskMapper);
    }

    @Test
    void updateTask_WithNewStatus_ShouldInvokeKafkaProducer() {
        UpdateTaskRequest updateTaskRequest = getUpdateTaskRequest();
        Task task = TestModelFactory.getTask();
        TaskDto taskDto = getTaskDto();
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        doAnswer(invocation -> {
            Task argTask = invocation.getArgument(0);
            argTask.setStatus(UPDATED_TASK_STATUS);
            return null;
        }).when(taskMapper).updateTask(task, updateTaskRequest);
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.updateTask(TASK_ID, updateTaskRequest);
        assertThat(result).isEqualTo(taskDto);
        verify(taskRepository).findById(TASK_ID);
        verify(taskMapper).updateTask(task, updateTaskRequest);
        verify(taskRepository).save(task);
        verify(kafkaProducer).send(getTaskStatusUpdatedEvent());
        verify(taskMapper).taskToTaskDto(task);
    }

    @Test
    void updateTask_WithoutNewStatus_ShouldNotInvokeKafkaProducer() {
        UpdateTaskRequest updateTaskRequest = getUpdateTaskRequest();
        Task task = TestModelFactory.getTask();
        TaskDto taskDto = getTaskDto();
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.updateTask(TASK_ID, updateTaskRequest);
        assertThat(result).isEqualTo(taskDto);
        verify(taskRepository).findById(TASK_ID);
        verify(taskMapper).updateTask(task, updateTaskRequest);
        verify(taskRepository).save(task);
        verify(taskMapper).taskToTaskDto(task);
        verifyNoInteractions(kafkaProducer);
    }

    @Test
    void updateTask_IfTaskNotFound_ShouldThrowException() {
        UpdateTaskRequest updateTaskRequest = getUpdateTaskRequest();
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(TASK_ID, updateTaskRequest));
        verify(taskRepository).findById(any());
        verifyNoMoreInteractions(taskRepository);
        verifyNoInteractions(taskMapper);
        verifyNoInteractions(kafkaProducer);
    }

    @Test
    void deleteTask() {
        when(taskRepository.deleteTaskById(TASK_ID)).thenReturn(1);

        String result = taskService.deleteTask(TASK_ID);
        assertThat(result).isEqualTo("Task has been deleted.");
        verify(taskRepository).deleteTaskById(any());
    }

    @Test
    void deleteTask_IfTaskNotExists() {
        when(taskRepository.deleteTaskById(TASK_ID)).thenReturn(0);

        String result = taskService.deleteTask(TASK_ID);
        assertThat(result).isEqualTo("Task was not found.");
        verify(taskRepository).deleteTaskById(any());
    }
}