package ru.bio4j.spring.ibus.kafka.tools;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.TopicConfig;
import ru.bio4j.spring.model.transport.errors.BioError;

import java.util.*;
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

    public void dropTopics(Set<String> topicNames, boolean silent) {
        try(Admin admin = Admin.create(Utils.adminConfigs(adminToolsProperties))) {
            DeleteTopicsResult result = admin.deleteTopics(topicNames);
            result.all().get();
        } catch (Throwable e) {
            if(!silent) {
                throw new RuntimeException(e);
            }
        }
    }

    public Set<String> listTopics() {
        try(Admin admin = Admin.create(Utils.adminConfigs(adminToolsProperties))) {
            ListTopicsOptions listTopicsOptions = new ListTopicsOptions().listInternal(false);
            ListTopicsResult result = admin.listTopics(listTopicsOptions);
            return result.names().get();
        } catch (InterruptedException | ExecutionException e) {
            throw BioError.wrap(e);
        }
    }

    public Set<String> createPartitions() {

}
