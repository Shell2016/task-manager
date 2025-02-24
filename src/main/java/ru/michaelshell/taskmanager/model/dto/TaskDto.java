package ru.michaelshell.taskmanager.model.dto;

import lombok.Builder;

@Builder
public record TaskDto(Long id,
                      String title,
                      String description,
                      Long userId,
                      TaskStatus status) {
}
