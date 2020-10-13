package ru.bio4j.spring.dba;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.bio4j.spring.model.CacheProperties;

@ConfigurationProperties(prefix= "ehcache")
public class TestCacheProperties extends CacheProperties {
}
