package ru.michaelshell.taskmanager.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateTaskRequest(@NotBlank
                                String title,
                                String description,
                                @NotNull
                                TaskStatus status) {
}
