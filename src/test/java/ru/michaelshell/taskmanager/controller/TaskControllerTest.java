package ru.michaelshell.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.michaelshell.taskmanager.kafka.producer.KafkaProducer;
import ru.michaelshell.taskmanager.model.dto.CreateTaskRequest;
import ru.michaelshell.taskmanager.model.dto.TaskDto;
import ru.michaelshell.taskmanager.model.dto.TaskStatus;
import ru.michaelshell.taskmanager.model.dto.UpdateTaskRequest;
import ru.michaelshell.taskmanager.model.entity.Task;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class TaskControllerTest extends IntegrationTestBase {

    private static final long TASK1_ID = 1L;
    private static final long TASK1_USER_ID = 101L;
    private static final String TASK1_TITLE = "Task 1";
    private static final String TASK1_DESCRIPTION = "Description for task 1";
    private static final TaskStatus TASK1_STATUS = TaskStatus.TODO;
    private static final long TASK2_ID = 2L;
    private static final long TASK2_USER_ID = 102L;
    private static final String TASK2_TITLE = "Task 2";
    private static final String TASK2_DESCRIPTION = "Description for task 2";
    private static final TaskStatus TASK2_STATUS = TaskStatus.IN_PROGRESS;
    private static final long NEW_TASK_ID = 3L;
    private static final long NEW_TASK_USER_ID = 5L;
    private static final String NEW_TASK_TITLE = "new task";
    private static final String NEW_TASK_DESCRIPTION = "new task description";
    private static final String TASK1_UPDATED_TITLE = "updated title";
    private static final String TASK1_UPDATED_DESCRIPTION = "updated description";
    private static final TaskStatus TASK1_UPDATED_STATUS = TaskStatus.TEST;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private KafkaProducer kafkaProducer;

    @Test
    void createTask() throws Exception {
        CreateTaskRequest createTaskRequest = CreateTaskRequest.builder()
                .userId(NEW_TASK_USER_ID)
                .title(NEW_TASK_TITLE)
                .description(NEW_TASK_DESCRIPTION)
                .build();
        TaskDto expected = TaskDto.builder()
                .id(NEW_TASK_ID)
                .userId(NEW_TASK_USER_ID)
                .title(NEW_TASK_TITLE)
                .description(NEW_TASK_DESCRIPTION)
                .status(TaskStatus.TODO)
                .build();

        mockMvc.perform(post("/tasks")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskRequest)))
                .andExpectAll(
                        content().contentType(APPLICATION_JSON),
                        status().isCreated(),
                        content().json(objectMapper.writeValueAsString(expected), JsonCompareMode.LENIENT));
    }

    @ParameterizedTest
    @MethodSource("getInvalidCreateTaskArguments")
    void createTask_WithInvalidArguments_ShouldReturnBadRequestStatus(CreateTaskRequest createTaskRequest) throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpectAll(
                        content().contentType(APPLICATION_JSON),
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(List.of(getTask1(), getTask2())))
                );
    }

    @Test
    void getTask() throws Exception {
        mockMvc.perform(get("/tasks/2"))
                .andExpectAll(
                        content().contentType(APPLICATION_JSON),
                        status().isOk(),
                        jsonPath("$.userId").value(TASK2_USER_ID),
                        jsonPath("$.title").value(TASK2_TITLE),
                        jsonPath("$.description").value(TASK2_DESCRIPTION),
                        jsonPath("$.status").value(TASK2_STATUS.name())
                );
    }

    @Test
    void getTask_WithInvalidId_ShouldReturnNotFoundStatus() throws Exception {
        mockMvc.perform(get("/tasks/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_WithSameStatus_ShouldNotInvokeKafkaProducer() throws Exception {
        UpdateTaskRequest updateTaskRequest = UpdateTaskRequest.builder()
                .title(TASK1_UPDATED_TITLE)
                .description(TASK1_UPDATED_DESCRIPTION)
                .status(TASK1_STATUS)
                .build();
        TaskDto result = TaskDto.builder()
                .id(TASK1_ID)
                .userId(TASK1_USER_ID)
                .title(TASK1_UPDATED_TITLE)
                .description(TASK1_UPDATED_DESCRIPTION)
                .status(TASK1_STATUS)
                .build();

        mockMvc.perform(
                        put("/tasks/1")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateTaskRequest))
                )
                .andExpectAll(
                        status().isOk(),
                        content().contentType(APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(result))
                );
        verifyNoInteractions(kafkaProducer);
    }

    @Test
    void updateTask_WithNewStatus_ShouldInvokeKafkaProducer() throws Exception {
        UpdateTaskRequest updateTaskRequest = UpdateTaskRequest.builder()
                .title(TASK1_UPDATED_TITLE)
                .description(TASK1_UPDATED_DESCRIPTION)
                .status(TASK1_UPDATED_STATUS)
                .build();
        TaskDto result = TaskDto.builder()
                .id(TASK1_ID)
                .userId(TASK1_USER_ID)
                .title(TASK1_UPDATED_TITLE)
                .description(TASK1_UPDATED_DESCRIPTION)
                .status(TASK1_UPDATED_STATUS)
                .build();

        mockMvc.perform(
                        put("/tasks/1")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateTaskRequest))
                )
                .andExpectAll(
                        status().isOk(),
                        content().contentType(APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(result))
                );
        verify(kafkaProducer).send(any());
    }

    @Test
    void updateTask_IfTaskNotFound_ShouldReturnStatusNotFound() throws Exception {
        UpdateTaskRequest updateTaskRequest = UpdateTaskRequest.builder()
                .title(TASK1_UPDATED_TITLE)
                .description(TASK1_UPDATED_DESCRIPTION)
                .status(TASK1_UPDATED_STATUS)
                .build();

        mockMvc.perform(
                        put("/tasks/3")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateTaskRequest))
                )
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("getInvalidUpdateTaskArguments")
    void updateTask_WithInvalidArguments_ShouldReturnStatusBadRequest(UpdateTaskRequest updateTaskRequest) throws Exception {
        mockMvc.perform(
                        put("/tasks/1")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateTaskRequest))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTask() throws Exception {
        mockMvc.perform(delete("/tasks/2"))
                .andExpectAll(
                        status().isOk(),
                        content().string("Task has been deleted.")
                );
    }

    @Test
    void deleteTask_IfTaskNotFound() throws Exception {
        mockMvc.perform(delete("/tasks/3"))
                .andExpectAll(
                        status().isOk(),
                        content().string("Task was not found.")
                );
    }

    private Task getTask1() {
        return Task.builder()
                .id(TASK1_ID)
                .userId(TASK1_USER_ID)
                .title(TASK1_TITLE)
                .description(TASK1_DESCRIPTION)
                .status(TASK1_STATUS)
                .build();
    }

    private Task getTask2() {
        return Task.builder()
                .id(TASK2_ID)
                .userId(TASK2_USER_ID)
                .title(TASK2_TITLE)
                .description(TASK2_DESCRIPTION)
                .status(TASK2_STATUS)
                .build();
    }

    private static Stream<Arguments> getInvalidCreateTaskArguments() {
        CreateTaskRequest createTaskRequestWithIdNull = CreateTaskRequest.builder()
                .title(NEW_TASK_TITLE)
                .description(NEW_TASK_DESCRIPTION)
                .build();
        CreateTaskRequest createTaskRequestWithTitleNull = CreateTaskRequest.builder()
                .userId(NEW_TASK_USER_ID)
                .description(NEW_TASK_DESCRIPTION)
                .build();
        CreateTaskRequest createTaskRequestWithTitleBlank = CreateTaskRequest.builder()
                .userId(NEW_TASK_USER_ID)
                .title("  ")
                .description(NEW_TASK_DESCRIPTION)
                .build();

        return Stream.of(
                Arguments.of(createTaskRequestWithIdNull),
                Arguments.of(createTaskRequestWithTitleBlank),
                Arguments.of(createTaskRequestWithTitleNull)
        );
    }

    private static Stream<Arguments> getInvalidUpdateTaskArguments() {
        UpdateTaskRequest updateTaskRequestWithTitleBlank = UpdateTaskRequest.builder()
                .title("   ")
                .description(TASK1_UPDATED_DESCRIPTION)
                .status(TASK1_UPDATED_STATUS)
                .build();
        UpdateTaskRequest updateTaskRequestWithTitleNull = UpdateTaskRequest.builder()
                .description(TASK1_UPDATED_DESCRIPTION)
                .status(TASK1_UPDATED_STATUS)
                .build();
        UpdateTaskRequest updateTaskRequestWithStatusNull = UpdateTaskRequest.builder()
                .title(TASK1_UPDATED_TITLE)
                .description(TASK1_UPDATED_DESCRIPTION)
                .build();

        return Stream.of(
                Arguments.of(updateTaskRequestWithStatusNull),
                Arguments.of(updateTaskRequestWithTitleBlank),
                Arguments.of(updateTaskRequestWithTitleNull)
        );
    }
}
