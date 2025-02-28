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
    /**
     * Если у нас несколько реплик, то при acks=all, значение minInsyncReplicas должно быть меньше чем replicationFactor,
     * иначе при потере даже одной реплики ничего записать будет нельзя.<p>
     * Пример нормальной конфигурации:<p>
     * acks=all
     * replicationFactor=3
     * minInsyncReplicas=2
     */
    private Short minInsyncReplicas;
    private Producer producer;
    private Consumer consumer;

    @Data
    public static class Producer {
        private String acks;
        /**
         * Задержка перед повторным подключением к брокеру, если соединение разорвано.
         */
        private String retryBackoffMs;
        /**
         * Время ожидания ответа от Kafka перед повторной попыткой
         */
        private String requestTimeoutMs;
        /**
         * Сколько максимум времени Producer пытается отправить сообщение.
         */
        private String deliveryTimeoutMs;
        /**
         * Сколько байтов сообщений накапливает Producer перед отправкой в Kafka.
         * Дефолтное значение 16кб.
         */
        private String batchSize;
        /**
         * Сколько времени продюсер может накапливать сообщения перед отправкой.
         * Отправка сработает либо по lingerMs, либо по batchSize.
         */
        private String lingerMs;
    }

    @Data
    public static class Consumer {
        private String groupId;
        private String trustedPackages;
        private String heartbeatTimeoutMs;
        /**
         * Если Consumer не отправляет heartbeat дольше этого времени,
         * Kafka считает его умершим и перераспределяет партиции (ребаланс).
         */
        private String sessionTimeoutMs;
        /**
         * Максимальное время между вызовами poll() у консюмера.
         * При превышении консюмер будет удален из группы и произойдет ребаланс.
         */
        private String maxPollIntervalMs;
        /**
         * Определяет, сколько сообщений за раз Consumer читает из Kafka.
         */
        private String maxPollRecords;
        /**
         * Определяет, что делать, если нет сохранённого offset (например, Consumer новый).<p>
         * "earliest" → Читает с начала топика (даже старые сообщения).<p>
         * "latest" → Читает только новые сообщения (старые игнорируются).<p>
         * "none" → Ошибка, если offset отсутствует.
         */
        private String autoOffsetReset;
        /**
         * Вкл/выкл автоматического коммита оффсета.
         */
        private String enableAutoCommit;
        /**
         * Используется в true обычно когда у нас в одном топике есть разные типы сообщений.
         */
        private String useTypeInfoHeaders;
    }
}
