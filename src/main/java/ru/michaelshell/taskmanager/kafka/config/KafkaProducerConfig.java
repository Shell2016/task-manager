package ru.michaelshell.taskmanager.kafka.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.michaelshell.taskmanager.model.event.TaskStatusUpdatedEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaConfigData kafkaConfigData;

    @Bean
    public ProducerFactory<String, TaskStatusUpdatedEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfigData.getProducer().getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfigData.getProducer().getValueSerializer());
        props.put(ProducerConfig.ACKS_CONFIG, kafkaConfigData.getProducer().getAcks());
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaConfigData.getProducer().getRetries());
        props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, kafkaConfigData.getProducer().getRetryBackoffMs());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaConfigData.getProducer().getBatchSize());
        props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaConfigData.getProducer().getLingerMs());
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, TaskStatusUpdatedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
