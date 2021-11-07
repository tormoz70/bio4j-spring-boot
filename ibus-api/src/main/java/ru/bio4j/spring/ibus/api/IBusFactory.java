package ru.bio4j.spring.ibus.api;

public interface IBusFactory<K, V> {
    IBusMessageHandler<K, V> createMessageHandler();
    IBusConsumer createConsumer();
    IBusConsumer createProducer();
}
