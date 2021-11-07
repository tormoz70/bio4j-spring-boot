package ru.bio4j.spring.ibus.kafka.tools;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminToolsProperties {
    private String bootstrapServer;
    private int retries;
    private int requestTimeoutMs;
}
