package ru.bio4j.spring.ibus.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IBusProducerProperies {
    private String bootstrapServer;
    private String clientId;
    private String topicName;
    private int topicPartitions;
    private short topicReplicationFactor;
    private String acks;
    private int retries;
    private int batchSize;
    private int lingerMs;
    private int bufferMemory;
    private Class<?> partitioner;
}
