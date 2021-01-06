package ru.bio4j.spring.isrvt.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface MessageHandler<K, V> {

    void process(ConsumerRecords<K, V> records);

}
