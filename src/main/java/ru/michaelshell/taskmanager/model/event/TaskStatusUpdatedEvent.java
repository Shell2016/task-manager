package ru.michaelshell.taskmanager.model.event;

import lombok.Builder;
import ru.michaelshell.taskmanager.model.dto.TaskStatus;

@Builder
public record TaskStatusUpdatedEvent(Long id,
                                     TaskStatus status) {
}
