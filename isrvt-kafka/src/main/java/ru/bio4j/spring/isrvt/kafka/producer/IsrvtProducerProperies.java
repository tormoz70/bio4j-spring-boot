package ru.bio4j.spring.isrvt.kafka.producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class IsrvtProducerProperies {

    private Map<String, Object> props;

    private String bootstrapServer;

    private String clientId;

    private String acks;

    private int retries;

    private int batchSize;

    private int lingerMs;

    private int bufferMemory;

    public String getBootstrapServer() {
        return bootstrapServer;
    }

    public void setBootstrapServer(String bootstrapServer) {
        this.bootstrapServer = bootstrapServer;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getLingerMs() {
        return lingerMs;
    }

    public void setLingerMs(int lingerMs) {
        this.lingerMs = lingerMs;
    }

    public int getBufferMemory() {
        return bufferMemory;
    }

    public void setBufferMemory(int bufferMemory) {
        this.bufferMemory = bufferMemory;
    }

    public Map<String, Object> producerConfigs() {
        if (props == null) {
            props = new HashMap<>();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServer());
            props.put(ProducerConfig.CLIENT_ID_CONFIG, getClientId());
            props.put(ProducerConfig.ACKS_CONFIG, getAcks());
            props.put(ProducerConfig.RETRIES_CONFIG, getRetries());
            props.put(ProducerConfig.BATCH_SIZE_CONFIG, getBatchSize());
            props.put(ProducerConfig.LINGER_MS_CONFIG, getLingerMs());
            props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, getBufferMemory());
//            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
//            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageSerializer.class);
        }
        return props;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "IsrvtProducerProperies{" +
                "props=" + props +
                ", bootstrapServer='" + bootstrapServer + '\'' +
                ", clientId='" + clientId + '\'' +
                ", acks='" + acks + '\'' +
                ", retries=" + retries +
                ", batchSize=" + batchSize +
                ", lingerMs=" + lingerMs +
                ", bufferMemory=" + bufferMemory +
                '}';
    }
}
