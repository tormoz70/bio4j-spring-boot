package ru.bio4j.spring.isrvt.kafka.consumer;

public interface MessageHandlerFactory<K, V> {

    MessageHandler<K, V> create();

}
