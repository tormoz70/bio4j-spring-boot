package ru.bio4j.spring.ibus.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IBusConsumerProperies {
    private String topicName;
    private int threadPoolSize;
    private String bootstrapServer;
    private String groupId;
    private int sessionTimeoutMs;
    private int heartbeatIntervalMs;
}
