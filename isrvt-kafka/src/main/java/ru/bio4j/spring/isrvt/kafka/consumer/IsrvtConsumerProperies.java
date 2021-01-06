package ru.bio4j.spring.isrvt.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class IsrvtConsumerProperies {

    private Map<String, Object> props;

    @Value("${upldr.consumer.topicName}")
    private String topicName;

    @Value("${upldr.consumer.threadPoolSize}")
    private int threadPoolSize;

    @Value("${upldr.consumer.bootstrapServer}")
    private String bootstrapServer;

    @Value("${upldr.consumer.clientId}")
    private String clientId;

    @Value("${upldr.consumer.groupId}")
    private String groupId;

    @Value("${upldr.consumer.sessionTimeoutMs}")
    private int sessionTimeoutMs;

    @Value("${upldr.consumer.heartbeatIntervalMs}")
    private int heartbeatIntervalMs;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public String getBootstrapServer() {
        return bootstrapServer;
    }

    public void setBootstrapServer(String bootstrapServer) {
        this.bootstrapServer = bootstrapServer;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public int getHeartbeatIntervalMs() {
        return heartbeatIntervalMs;
    }

    public void setHeartbeatIntervalMs(int heartbeatIntervalMs) {
        this.heartbeatIntervalMs = heartbeatIntervalMs;
    }

    public Map<String, Object> consumerConfig() {
        if(props == null) {
            props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServer());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, getGroupId());
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, getSessionTimeoutMs());
            if(getHeartbeatIntervalMs() > 0)
                props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, getHeartbeatIntervalMs());
//            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
//            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        }
        return props;
    }

    @Override
    public String toString() {
        return "IsrvtConsumerProperies{" +
                "props=" + props +
                ", topicName='" + topicName + '\'' +
                ", threadPoolSize=" + threadPoolSize +
                ", bootstrapServer='" + bootstrapServer + '\'' +
                ", clientId='" + clientId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", sessionTimeoutMs=" + sessionTimeoutMs +
                ", heartbeatIntervalMs=" + heartbeatIntervalMs +
                '}';
    }

}
