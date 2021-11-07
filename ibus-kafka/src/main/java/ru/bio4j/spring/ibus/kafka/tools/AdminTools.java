package ru.bio4j.spring.ibus.kafka.tools;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.TopicConfig;
import ru.bio4j.spring.model.transport.errors.BioError;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AdminTools {

    private final AdminToolsProperties adminToolsProperties;

    public AdminTools(AdminToolsProperties adminToolsProperties) {
        this.adminToolsProperties = adminToolsProperties;
    }

    public void createTopic(String topicName, int partitions, short replicationFactory, Map<String, String> newTopicConfig) {
        try(Admin admin = Admin.create(Utils.adminConfigs(adminToolsProperties))) {
            if(newTopicConfig == null) {
                newTopicConfig = new HashMap<>();
                newTopicConfig.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT);
            }

            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactory);
            CreateTopicsResult result = admin.createTopics(Collections.singleton(newTopic));
            KafkaFuture<Void> future = result.values().get(topicName);
            future.get();
        } catch (InterruptedException | ExecutionException e) {
           throw BioError.wrap(e);
        }
    }

    public void createTopic(String topicName, int partitions, short replicationFactory) {
        createTopic(topicName, partitions, replicationFactory, null);
    }

    public void createTopic(String topicName, int partitions) {
        createTopic(topicName, partitions, (short)1);
    }

    public void createTopic(String topicName) {
        createTopic(topicName, 1, (short)1);
    }
}
