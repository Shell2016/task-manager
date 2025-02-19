package ru.michaelshell.taskmanager.model.dto;

import lombok.Builder;

@Builder
public record TaskDto(long id,
                      String title,
                      String description,
                      long userId) {
}
