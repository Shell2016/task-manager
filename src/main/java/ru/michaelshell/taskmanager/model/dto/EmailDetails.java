package ru.michaelshell.taskmanager.model.dto;

import lombok.Builder;

@Builder
public record EmailDetails(String recipient,
                           String msgBody,
                           String subject) {
}
