package ru.bio4j.spring.ibus.kafka.tools;


import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

public class AdminToolsTest {

    @Test
    public void createTopic() {
        final String testTopicName = "test-adminTools-topic";
        final int testParts = 300;
        AdminToolsProperties properties = AdminToolsProperties.builder()
                .bootstrapServer("192.168.70.33:9092")
                .requestTimeoutMs(1000)
                .retries(1)
                .build();
        AdminTools adminTools = new AdminTools(properties);
        adminTools.dropTopics(Sets.newHashSet(testTopicName), false);
        adminTools.createTopic(testTopicName);
        Set<String> topics = adminTools.listTopics();
        Assert.assertEquals(topics.stream().filter(t -> t.equals(testTopicName)).findFirst().orElse(null), testTopicName);
        adminTools.addPartitions(testTopicName, testParts);
        Map<String, Integer> partCount = adminTools.getPartitionCount(topics);
        Assert.assertEquals(testParts, partCount.get(testTopicName).intValue());
    }

}