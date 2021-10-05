package ru.bio4j.spring.helpers.cache.impl;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.distribution.RMICacheManagerPeerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cloud.commons.util.InetUtils;
import ru.bio4j.spring.commons.types.ApplicationContextProvider;

import java.net.UnknownHostException;

public class EurekaRMICacheManagerPeerListener extends RMICacheManagerPeerListener {
    private static final Logger LOG = LoggerFactory.getLogger(EurekaRMICacheManagerPeerListener.class.getName());

    private InetUtils inetUtils;

    public EurekaRMICacheManagerPeerListener(String hostName, Integer port, Integer remoteObjectPort, CacheManager cacheManager,
                                             Integer socketTimeoutMillis) throws UnknownHostException {
        super(hostName, port, remoteObjectPort, cacheManager, socketTimeoutMillis);
    }

    @Override
    protected String calculateHostAddress() throws UnknownHostException {
        if (inetUtils == null)
            try {
                inetUtils = ApplicationContextProvider.getBean(InetUtils.class);
            } catch (NoSuchBeanDefinitionException ex) {
                LOG.warn("Can't find bean {}. Falling back to default implementation of {}.", InetUtils.class.getName(), RMICacheManagerPeerListener.class.getName());
                return super.calculateHostAddress();
            }
        InetUtils.HostInfo hostInfo = inetUtils.findFirstNonLoopbackHostInfo();
        return hostInfo.getIpAddress();
    }
}
