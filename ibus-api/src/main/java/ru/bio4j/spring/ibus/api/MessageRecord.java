package ru.bio4j.spring.ibus.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageRecord<K, V> {
    private K key;
    private V value;
    private int partition;
    private long offset;
    private String topic;

}
