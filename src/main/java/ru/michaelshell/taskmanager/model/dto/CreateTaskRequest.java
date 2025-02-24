package ru.michaelshell.taskmanager.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateTaskRequest(@NotBlank
                                String title,
                                String description,
                                @NotNull
                                Long userId) {
}
