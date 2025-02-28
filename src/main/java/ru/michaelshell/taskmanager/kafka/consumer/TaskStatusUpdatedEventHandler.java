package ru.michaelshell.taskmanager.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.michaelshell.taskmanager.kafka.config.KafkaConfigData;
import ru.michaelshell.taskmanager.model.event.TaskStatusUpdatedEvent;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskStatusUpdatedEventHandler {

    private final KafkaConfigData config;

    @KafkaListener(topics = "task-status-updated-events-topic")
    public void handle(@Payload List<TaskStatusUpdatedEvent> events,
                       Acknowledgment ack) {
        log.info("Received batch of {} messages", events.size());
        events.forEach(System.out::println);
        ack.acknowledge();
    }
}
