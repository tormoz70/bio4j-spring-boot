package ru.bio4j.spring.isrvt.kafka.producer;

public interface IsrvtProducer<K, V> {
    void send(String topic, int partition, K key, V message);
    void send(String topic, K key, V message);
}
