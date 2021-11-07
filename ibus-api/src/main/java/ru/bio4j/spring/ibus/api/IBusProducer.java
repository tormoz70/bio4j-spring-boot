package ru.bio4j.spring.ibus.api;

public interface IBusProducer<K, V> {
    void send(K key, V message);
}
