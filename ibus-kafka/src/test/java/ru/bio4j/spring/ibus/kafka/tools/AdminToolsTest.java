package ru.bio4j.spring.ibus.kafka.tools;


import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

public class AdminToolsTest {

    @Test
    public void createTopic() {
        String testTopicName = "test-adminTools-topic";
        AdminToolsProperties properties = AdminToolsProperties.builder()
                .bootstrapServer("192.168.70.33:9092")
                .requestTimeoutMs(1000)
                .retries(1)
                .build();
        AdminTools adminTools = new AdminTools(properties);
        adminTools.dropTopics(Sets.newHashSet(testTopicName), true);
        adminTools.createTopic(testTopicName);
        Set<String> topics = adminTools.listTopics();
        Assert.assertEquals(topics.stream().filter(t -> t.equals(testTopicName)).findFirst().orElse(null), testTopicName);

    }

}