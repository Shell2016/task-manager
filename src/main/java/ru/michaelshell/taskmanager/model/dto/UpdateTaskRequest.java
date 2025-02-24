package ru.michaelshell.taskmanager.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateTaskRequest(@NotBlank
                                String title,
                                String description) {
}
