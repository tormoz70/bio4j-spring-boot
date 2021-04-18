package ru.bio4j.spring.isrvt.kafka.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsumerRunner<K, V> extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(ConsumerRunner.class);

    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final ConsumerServiceProperties properties;
    private final MessageHandler<K, V> messageHandler;
    private final Deserializer<K> keyDeserializer;
    private final Deserializer<V> messageDeserializer;

    public ConsumerRunner(ConsumerServiceProperties properties, MessageHandlerFactory messageHandlerFactory, Deserializer<K> keyDeserializer, Deserializer<V> messageDeserializer) {
        this.properties = properties;
        this.keyDeserializer = keyDeserializer;
        this.messageDeserializer = messageDeserializer;
        this.messageHandler = messageHandlerFactory.create();
//        this.setName("thread-"+properties.getClientId());
    }

    @Override
    public void run() {
        LOG.info(String.format("Starting consumer subscription (topic: %s)...", properties.getTopicName()));
        try(Consumer<K, V> consumer = new KafkaConsumer<>(properties.consumerConfig(), keyDeserializer, messageDeserializer)) {
            consumer.subscribe(Arrays.asList(properties.getTopicName()));
            while (!closed.get()) {
                ConsumerRecords<K, V> recs = consumer.poll(Duration.ofMillis(10_000));
                if(recs.count() > 0) {
                    messageHandler.process(recs);
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