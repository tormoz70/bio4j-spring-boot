package ru.bio4j.spring.helpers.cache.impl;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.distribution.CacheManagerPeerProvider;
import net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory;
import net.sf.ehcache.util.PropertyUtil;
import ru.bio4j.spring.commons.utils.Strings;

import java.util.Properties;

public class EurekaRMICacheManagerPeerProviderFactory extends RMICacheManagerPeerProviderFactory {
    private static final String SERVICE_NAME = "serviceName";
    private static final String REFRESH_INTERVAL = "refreshInterval";

    @Override
    protected CacheManagerPeerProvider createAutomaticallyConfiguredCachePeerProvider(CacheManager cacheManager,
                                                                                      Properties properties) {
        String serviceName = PropertyUtil.extractAndLogProperty(SERVICE_NAME, properties);
        if (Strings.isNullOrEmpty(serviceName))
            throw new RuntimeException("Property '" + SERVICE_NAME + "' not set.");
        String refreshIntervalString = PropertyUtil.extractAndLogProperty(REFRESH_INTERVAL, properties);
        long refreshInterval;
        try {
            refreshInterval = Long.parseLong(refreshIntervalString);
        } catch (NumberFormatException ex) {
            refreshInterval = 2000;
        }
        return new EurekaRMICacheManagerPeerProvider(cacheManager, serviceName, refreshInterval);
    }
}
