package ru.bio4j.spring.helpers.cache.impl;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.distribution.CacheManagerPeerListener;
import net.sf.ehcache.distribution.RMICacheManagerPeerListenerFactory;

import java.net.UnknownHostException;

public class EurekaRMICacheManagerPeerListenerFactory extends RMICacheManagerPeerListenerFactory {
    @Override
    protected CacheManagerPeerListener doCreateCachePeerListener(String hostName,
                                                                 Integer port,
                                                                 Integer remoteObjectPort,
                                                                 CacheManager cacheManager,
                                                                 Integer socketTimeoutMillis) {
        try {
            return new EurekaRMICacheManagerPeerListener(hostName, port, remoteObjectPort, cacheManager, socketTimeoutMillis);
        } catch (UnknownHostException e) {
            throw new CacheException("Unable to create CacheManagerPeerListener. Initial cause was " + e.getMessage(), e);
        }
    }
}
