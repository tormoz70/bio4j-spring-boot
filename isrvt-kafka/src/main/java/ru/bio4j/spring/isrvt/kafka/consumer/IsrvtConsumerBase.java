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
    private final List<IsrvtConsumerRunner> runners;
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
        for(int i=0; i<properies.getThreadPoolSize(); i++) {
            runners.add(new IsrvtConsumerRunner<>(properies, messageHandlerFactory, keyDeserializer, messageDeserializer));
        }
        runners.forEach(r -> r.run());
    }


    public void stopConsume() {
        runners.forEach(r -> r.stop());
        runners.clear();
    }

    @PreDestroy
    public void destroy() {
        System.out.println(
                "Callback triggered - @PreDestroy.");
        stopConsume();
    }
}
