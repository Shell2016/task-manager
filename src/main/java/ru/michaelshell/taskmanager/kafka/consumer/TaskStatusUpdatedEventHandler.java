package ru.michaelshell.taskmanager.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.michaelshell.taskmanager.config.EmailProperties;
import ru.michaelshell.taskmanager.model.dto.EmailDetails;
import ru.michaelshell.taskmanager.model.event.TaskStatusUpdatedEvent;
import ru.michaelshell.taskmanager.service.EmailNotificationService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskStatusUpdatedEventHandler {

    private final EmailNotificationService emailNotificationService;
    private final EmailProperties emailProperties;

    @KafkaListener(topics = "task-status-updated-events-topic")
    public void handle(@Payload List<TaskStatusUpdatedEvent> events,
                       Acknowledgment ack) {
        log.info("Received batch of {} messages", events.size());
        for (TaskStatusUpdatedEvent event : events) {
            log.info("Processing event: {}", event);
            try {
                emailNotificationService.send(EmailDetails.builder()
                        .recipient(emailProperties.getRecipient())
                        .subject("Task status has changed")
                        .msgBody("Status of the task with id " + event.id() + " has changed to "
                                + event.status().name())
                        .build());
                log.info("Email was sent successfully!");
            } catch (Exception e) {
                log.error("Exception was thrown while sending email: {}", e.getMessage());
            }
        }
        ack.acknowledge(); //commit offset для всего батча
    }
}
