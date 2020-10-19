package ru.bio4j.spring.helpers.cache;


import org.springframework.beans.factory.annotation.Value;

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
