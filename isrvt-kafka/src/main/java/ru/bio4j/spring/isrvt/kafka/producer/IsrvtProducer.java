package ru.bio4j.spring.isrvt.kafka.producer;

public interface IsrvtProducer<K, V> {
    void send(K key, V message);
}
