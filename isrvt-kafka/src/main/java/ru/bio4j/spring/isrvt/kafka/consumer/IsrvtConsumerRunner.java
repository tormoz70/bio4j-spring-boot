package ru.bio4j.spring.isrvt.kafka.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.Deserializer;
import ru.bio4j.spring.commons.types.LogWrapper;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class IsrvtConsumerRunner<K, V> implements Runnable {
    private static final LogWrapper LOG = LogWrapper.getLogger(IsrvtConsumerRunner.class);

    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final IsrvtConsumerProperies properies;
    private final Consumer<K, V> consumer;
    private final MessageHandler<K, V> messageHandler;

    public IsrvtConsumerRunner(IsrvtConsumerProperies properties, MessageHandlerFactory messageHandlerFactory, Deserializer<K> keyDeserializer, Deserializer<V> messageDeserializer) {
        this.properies = properties;
        this.consumer = new KafkaConsumer<>(properties.consumerConfig(), keyDeserializer, messageDeserializer);
        this.consumer.subscribe(Arrays.asList(properties.getTopicName()));
        this.messageHandler = messageHandlerFactory.create();
    }

    @Override
    public void run() {
        try {
            while (!closed.get()) {
                messageHandler.process(consumer.poll(Duration.ofMillis(10_000)));
                consumer.commitSync();
            }
        } catch (WakeupException e) {
            if (!closed.get()) throw e;
        } finally {
            consumer.close();
        }
    }

    public void stop() {
        closed.set(true);
    }
}
