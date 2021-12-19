package ru.bio4j.spring.ibus.kafka.tools;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import ru.bio4j.spring.model.transport.errors.BioError;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
            checkResultFutures(result.values());
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

    public void addPartitions(String topicName, int numPartitions) {
        try (Admin admin = Admin.create(Utils.adminConfigs(adminToolsProperties))) {
            Map<String, NewPartitions> newPartitionSet = new HashMap<>();
            newPartitionSet.put(topicName, NewPartitions.increaseTo(numPartitions));
            CreatePartitionsResult result = admin.createPartitions(newPartitionSet);
            checkResultFutures(result.values());
        } catch (Exception e) {
            throw BioError.wrap(e);
        }
    }

    public Map<String, Integer> getPartitionCount(Set<String> topicNames) {
        try (Admin admin = Admin.create(Utils.adminConfigs(adminToolsProperties))) {
            DescribeTopicsResult topicsResult = admin.describeTopics(topicNames);
            Map<String, TopicDescription> topicDescriptions = new HashMap<>();
            for(Map.Entry<String, KafkaFuture<TopicDescription>> entry : topicsResult.values().entrySet()) {
                try {
                    topicDescriptions.put(entry.getKey(), entry.getValue().get());
                } catch(ExecutionException e) {
                    if (e.getCause() instanceof UnknownTopicOrPartitionException) {
                        // ignore
                    } else {
                        throw BioError.wrap(e);
                    }
                } catch (InterruptedException e) {
                    throw BioError.wrap(e);
                }
            }
            return topicDescriptions.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, it->it.getValue().partitions().size()));
        } catch (Exception e) {
            throw BioError.wrap(e);
        }
    }

    private void checkResultFutures(Map<?, KafkaFuture<Void>> futures) {
        try {
            for (Map.Entry<?, KafkaFuture<Void>> entry : futures.entrySet()) {
                KafkaFuture<Void> future = entry.getValue();
                future.get();
            }
        } catch (ExecutionException | InterruptedException e) {
            BioError.wrap(e);
        }
    }
}
