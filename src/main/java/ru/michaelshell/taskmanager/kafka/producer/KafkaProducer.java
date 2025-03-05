package ru.michaelshell.taskmanager.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.michaelshell.taskmanager.config.KafkaConfigProperties;
import ru.michaelshell.taskmanager.model.event.TaskStatusUpdatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, TaskStatusUpdatedEvent> kafkaTemplate;
    private final KafkaConfigProperties config;

    public void send(TaskStatusUpdatedEvent event) {
        ProducerRecord<String, TaskStatusUpdatedEvent> producerRecord =
                new ProducerRecord<>(config.getTopic(), event.id().toString(), event);

        kafkaTemplate.send(producerRecord)
                .whenComplete((result, e) -> {
                    if (e == null) {
                        log.info("Sent message {} to topic: {}  Partition: {}  Offset: {}",
                                event,
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Unable to send message: {} Error message: {}", event, e.getMessage());
                    }
                });
    }
}
