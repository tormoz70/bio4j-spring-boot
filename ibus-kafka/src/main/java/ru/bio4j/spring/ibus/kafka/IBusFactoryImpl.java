package ru.bio4j.spring.ibus.kafka;


import ru.bio4j.spring.ibus.api.IBusConsumer;
import ru.bio4j.spring.ibus.api.IBusFactory;
import ru.bio4j.spring.ibus.api.IBusMessageHandler;

public class IBusFactoryImpl<K, V> implements IBusFactory<K, V> {
    @Override
    public IBusMessageHandler<K, V> createMessageHandler() {
        return null;
    }

    @Override
    public IBusConsumer createConsumer() {
        return null;
    }

    @Override
    public IBusConsumer createProducer() {
        return null;
    }
}
