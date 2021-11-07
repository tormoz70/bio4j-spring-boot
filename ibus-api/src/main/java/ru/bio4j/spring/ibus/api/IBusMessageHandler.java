package ru.bio4j.spring.ibus.api;


public interface IBusMessageHandler<K, V> {

    void process(MessageRecord<K, V> record);

}
