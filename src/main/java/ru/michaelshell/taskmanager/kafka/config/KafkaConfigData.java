package ru.michaelshell.taskmanager.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfigData {

    private String bootstrapServers;
    private String topic;
    private Integer numberOfPartitions;
    private Short replicationFactor;
    private Short minInsyncReplicas;
    private Producer producer;
    private Consumer consumer;

    @Data
    public static class Producer {
        private String keySerializer;
        private String valueSerializer;
        private String acks;
        private String retryBackoffMs;
        private String requestTimeoutMs;
        private String deliveryTimeoutMs;
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
