package ru.bio4j.spring.ibus.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.ibus.api.IBusProducer;
import ru.bio4j.spring.ibus.api.IBusProducerProperies;
import ru.bio4j.spring.ibus.kafka.tools.Utils;

public class IBusProducerImpl<K, V> implements IBusProducer<K, V> {
    private static final Logger LOG = LoggerFactory.getLogger(IBusProducerImpl.class);

    private final IBusProducerProperies properies;
    private final Producer<K, V> producer;


    public IBusProducerImpl(IBusProducerProperies properies, Serializer<K> keySerializer, Serializer<V> messageSerializer) {
        this.properies = properies;
        this.producer = new KafkaProducer<>(Utils.producerConfigs(properies), keySerializer, messageSerializer);
    }

    @Override
    public void send(K key, V message) {
        if(LOG.isDebugEnabled()) LOG.debug(String.format("Try sending msg{key: %d; messageBody: %s} to topic: %s from service: %s...", key, message, properies.getTopicName(), properies.getClientId()));
        producer.send(new ProducerRecord(properies.getTopicName(), key, message));
        if(LOG.isDebugEnabled()) LOG.debug(String.format("Sent msg{key: %d; messageBody: %s} to topic: %s from service: %s!", key, message, properies.getTopicName(), properies.getClientId()));
    }

}
