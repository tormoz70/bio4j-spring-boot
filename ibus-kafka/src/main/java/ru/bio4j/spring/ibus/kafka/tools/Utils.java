package ru.bio4j.spring.ibus.kafka.tools;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import ru.bio4j.spring.ibus.api.IBusConsumerProperies;
import ru.bio4j.spring.ibus.api.IBusProducerProperies;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static Map<String, Object> adminConfigs(AdminToolsProperties producerProperies) {
        HashMap<String, Object> props = new HashMap<>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, producerProperies.getBootstrapServer());
//        props.put(AdminClientConfig.RETRIES_CONFIG, producerProperies.getRetries());
//        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, producerProperies.getRequestTimeoutMs());
        return props;
    }

    public static Map<String, Object> producerConfigs(IBusProducerProperies producerProperies) {
        HashMap<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerProperies.getBootstrapServer());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, producerProperies.getClientId());
        props.put(ProducerConfig.ACKS_CONFIG, producerProperies.getAcks());
        props.put(ProducerConfig.RETRIES_CONFIG, producerProperies.getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, producerProperies.getBatchSize());
        props.put(ProducerConfig.LINGER_MS_CONFIG, producerProperies.getLingerMs());
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, producerProperies.getBufferMemory());
        if(producerProperies.getPartitioner() != null)
            props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, producerProperies.getPartitioner().getName());
        return props;
    }

    public static Map<String, Object> consumerConfig(IBusConsumerProperies consumerProperies) {
        HashMap<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperies.getBootstrapServer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperies.getGroupId());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, consumerProperies.getSessionTimeoutMs());
        if(consumerProperies.getHeartbeatIntervalMs() > 0)
            props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, consumerProperies.getHeartbeatIntervalMs());
        return props;
    }


}
