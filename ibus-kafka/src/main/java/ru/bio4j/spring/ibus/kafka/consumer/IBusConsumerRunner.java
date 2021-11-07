package ru.bio4j.spring.ibus.kafka.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.ibus.api.IBusConsumerProperies;
import ru.bio4j.spring.ibus.api.IBusMessageHandler;
import ru.bio4j.spring.ibus.api.IBusFactory;
import ru.bio4j.spring.ibus.api.MessageRecord;
import ru.bio4j.spring.ibus.kafka.tools.Utils;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class IBusConsumerRunner<K, V> extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(IBusConsumerRunner.class);

    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final IBusConsumerProperies properties;
    private final IBusMessageHandler<K, V> IBusMessageHandler;
    private final Deserializer<K> keyDeserializer;
    private final Deserializer<V> messageDeserializer;

    public IBusConsumerRunner(IBusConsumerProperies properties, IBusFactory IBusFactory, Deserializer<K> keyDeserializer, Deserializer<V> messageDeserializer) {
        this.properties = properties;
        this.keyDeserializer = keyDeserializer;
        this.messageDeserializer = messageDeserializer;
        this.IBusMessageHandler = IBusFactory.createMessageHandler();
    }

    @Override
    public void run() {
        LOG.info(String.format("Starting consumer subscription (topic: %s)...", properties.getTopicName()));
        try(Consumer<K, V> consumer = new KafkaConsumer<>(Utils.consumerConfig(properties), keyDeserializer, messageDeserializer)) {
            consumer.subscribe(Arrays.asList(properties.getTopicName()));
            while (!closed.get()) {
                ConsumerRecords<K, V> recs = consumer.poll(Duration.ofMillis(10_000));
                if(recs.count() > 0) {
                    for(ConsumerRecord<K, V> record : recs) {
                        MessageRecord<K, V> messageRecord = new MessageRecord<>(record.key(), record.value(), record.partition(), record.offset(), record.topic());
                        IBusMessageHandler.process(messageRecord);
                    }
                    consumer.commitSync();
                }
            }
        } catch (WakeupException e) {
            if (!closed.get()) throw e;
        }
    }

    public void close() {
        closed.set(true);
    }
}
