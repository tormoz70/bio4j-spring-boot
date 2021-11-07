package ru.bio4j.spring.ibus.kafka.consumer;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.ibus.api.IBusConsumer;
import ru.bio4j.spring.ibus.api.IBusConsumerProperies;
import ru.bio4j.spring.ibus.api.IBusFactory;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

public class IBusConsumerImpl<K, V> implements IBusConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(IBusConsumerImpl.class);

    private final IBusConsumerProperies properies;
    private final List<IBusConsumerRunner<K, V>> runners;
    private final IBusFactory<K, V> ibusFactory;
    private final Deserializer<K> keyDeserializer;
    private final Deserializer<V> messageDeserializer;

    public IBusConsumerImpl(IBusConsumerProperies properies, IBusFactory<K, V> ibusFactory, Deserializer<K> keyDeserializer, Deserializer<V> messageDeserializer) {
        this.properies = properies;
        this.ibusFactory = ibusFactory;
        this.keyDeserializer = keyDeserializer;
        this.messageDeserializer = messageDeserializer;
        this.runners = new ArrayList<>();
    }

    @Override
    public void startConsume() {
        LOG.info("About starting consume...");
        LOG.info(String.format("Consumer properties: %s", properies));
        LOG.info("About init consumers pool...");
        for(int i=0; i<properies.getThreadPoolSize(); i++) {
            runners.add(new IBusConsumerRunner<K, V>(properies, ibusFactory, keyDeserializer, messageDeserializer));
        }
        LOG.info("Consumers pool initialized");
        LOG.info("About consumers pool running...");
        runners.forEach(r -> r.start());
        LOG.info("Consumers pool running is done");
    }


    @Override
    public void stopConsume() {
        LOG.info("About consumers pool stopping...");
        runners.forEach(r -> r.close());
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
