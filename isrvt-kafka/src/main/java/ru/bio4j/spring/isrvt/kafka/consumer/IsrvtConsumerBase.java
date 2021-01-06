package ru.bio4j.spring.isrvt.kafka.consumer;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bio4j.spring.commons.types.LogWrapper;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

public class IsrvtConsumerBase<K, V> {
    private static final LogWrapper LOG = LogWrapper.getLogger(IsrvtConsumerBase.class);

    private final IsrvtConsumerProperies properies;
    private final List<IsrvtConsumerRunner<K, V>> runners;
    private final MessageHandlerFactory messageHandlerFactory;
    private final Deserializer<K> keyDeserializer;
    private final Deserializer<V> messageDeserializer;

    @Autowired
    public IsrvtConsumerBase(IsrvtConsumerProperies properies, MessageHandlerFactory messageHandlerFactory, Deserializer<K> keyDeserializer, Deserializer<V> messageDeserializer) {
        this.properies = properies;
        this.messageHandlerFactory = messageHandlerFactory;
        this.keyDeserializer = keyDeserializer;
        this.messageDeserializer = messageDeserializer;
        this.runners = new ArrayList<>();
    }

    public void startConsume() {
        LOG.info("About starting consume...");
        LOG.info(String.format("Consumer properties: %s", properies));
        LOG.info("About init consumers pool...");
        for(int i=0; i<properies.getThreadPoolSize(); i++) {
            runners.add(new IsrvtConsumerRunner<>(properies, messageHandlerFactory, keyDeserializer, messageDeserializer));
        }
        LOG.info("Consumers pool initialized");
        LOG.info("About consumers pool running...");
        runners.forEach(r -> r.run());
        LOG.info("Consumers pool running is done");
    }


    public void stopConsume() {
        LOG.info("About consumers pool stopping...");
        runners.forEach(r -> r.stop());
        LOG.info("Consumers pool stopped");
        LOG.info("About consumers pool clearing...");
        runners.clear();
        LOG.info("Consumers pool cleared");
    }

    @PreDestroy
    public void destroy() {
        LOG.info("Callback triggered - @PreDestroy");
        stopConsume();
    }
}
