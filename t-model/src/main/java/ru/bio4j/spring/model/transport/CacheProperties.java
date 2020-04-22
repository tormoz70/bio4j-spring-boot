package ru.bio4j.spring.model.transport;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix= "ehcache")
public class CacheProperties {
    @Value("${ehcache.persistent.path}")
    private String cachePersistentPath;

    @Value("${ehcache.config}")
    private String cacheConfigFile;

    public String getCachePersistentPath() {
        return cachePersistentPath;
    }

    public void setCachePersistentPath(String cachePersistentPath) {
        this.cachePersistentPath = cachePersistentPath;
    }

    public String getCacheConfigFile() {
        return cacheConfigFile;
    }

    public void setCacheConfigFile(String cacheConfigFile) {
        this.cacheConfigFile = cacheConfigFile;
    }
}
