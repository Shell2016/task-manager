package ru.michaelshell.taskmanager.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfigData {

    private String bootstrapServers;
    private Integer numberOfPartitions;
    private Short replicationFactor;
    private Producer producer;
    private Consumer consumer;

    @Data
    public static class Producer {
        private String keySerializer;
        private String valueSerializer;
        private String acks;
        private String retries;
        private String retryBackoffMs;
        private String batchSize;
        private String lingerMs;
    }

    @Data
    public static class Consumer {
        private String keyDeserializer;
        private String valueDeserializer;
        private String groupId;
        private String trustedPackages;
    }
}
