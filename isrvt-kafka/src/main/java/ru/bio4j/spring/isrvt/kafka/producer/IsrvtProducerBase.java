package ru.bio4j.spring.isrvt.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class IsrvtProducerBase<K, V> {
    private static final Logger LOG = LoggerFactory.getLogger(IsrvtProducerBase.class);

    private final IsrvtProducerProperies properies;
    private final Producer<K, V> producer;


    @Autowired
    public IsrvtProducerBase(IsrvtProducerProperies properies, Serializer<K> keySerializer, Serializer<V> messageSerializer) {
        this.properies = properies;
        this.producer = new KafkaProducer<>(properies.producerConfigs(), keySerializer, messageSerializer);
    }

    public void send(K key, V message) {
        if(LOG.isDebugEnabled()) LOG.debug(String.format("Try sending msg{key: %d; messageBody: %s} to topic: %s from service: %s...", key, message, properies.getTopicName(), properies.getClientId()));
        producer.send(new ProducerRecord(properies.getTopicName(), key, message));
        if(LOG.isDebugEnabled()) LOG.debug(String.format("Sent msg{key: %d; messageBody: %s} to topic: %s from service: %s!", key, message, properies.getTopicName(), properies.getClientId()));
    }
}