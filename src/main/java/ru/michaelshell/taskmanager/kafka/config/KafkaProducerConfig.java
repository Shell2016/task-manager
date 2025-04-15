package ru.michaelshell.taskmanager.kafka.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.michaelshell.taskmanager.config.KafkaConfigProperties;
import ru.michaelshell.taskmanager.model.event.TaskStatusUpdatedEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaConfigProperties config;

    @Bean
    public ProducerFactory<String, TaskStatusUpdatedEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, config.getProducer().getAcks());
        props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, config.getProducer().getRetryBackoffMs());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, config.getProducer().getBatchSize());
        props.put(ProducerConfig.LINGER_MS_CONFIG, config.getProducer().getLingerMs());
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, config.getProducer().getRequestTimeoutMs());
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, config.getProducer().getDeliveryTimeoutMs());
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, TaskStatusUpdatedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
