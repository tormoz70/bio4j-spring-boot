package ru.bio4j.spring.helpers.cache.impl;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.distribution.CacheManagerPeerListener;
import net.sf.ehcache.distribution.CachePeer;
import net.sf.ehcache.distribution.RMICachePeer;
import net.sf.ehcache.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.commons.utils.Strings;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EurekaStatusChecker {
    private static final String METADATA_CACHES_KEY = "ehcache.names";
    private static final String METADATA_PORT_KEY = "ehcache.port";
    private static final String METADATA_CACHE_DELIMITER = "|";
    private static final String METADATA_CACHE_DELIMITER_REGEX = "\\|";
    private static final Logger LOG = LoggerFactory.getLogger(EurekaStatusChecker.class.getName());

    private final EurekaRMICacheManagerPeerProvider peerProvider;
    private final String serviceName;
    private final EurekaClient eurekaClient;
    private final CacheManager cacheManager;
    private final long refreshInterval;
    private final Set<String> rmiUrlsProcessingQueue = Collections.synchronizedSet(new HashSet<>());
    private final ApplicationInfoManager appInfoMng;

    private EurekaCheckerThread receiverThread;
    private ExecutorService processingThreadPool;
    private boolean stopped;

    public EurekaStatusChecker(EurekaRMICacheManagerPeerProvider peerProvider, String serviceName, long refreshInterval, EurekaClient eurekaClient, ApplicationInfoManager appInfoMng, CacheManager cacheManager) {
        this.peerProvider = peerProvider;
        this.serviceName = serviceName;
        this.eurekaClient = eurekaClient;
        this.appInfoMng = appInfoMng;
        this.cacheManager = cacheManager;
        this.refreshInterval = refreshInterval;
    }

    public static String getUrl(String hostname, String rmiRegistryPort, String cacheName) {
        return new StringBuilder()
                .append("//")
                .append(hostname)
                .append(":")
                .append(rmiRegistryPort)
                .append("/")
                .append(cacheName)
                .toString();
    }

    public long getRefreshInterval() {
        return refreshInterval;
    }

    final void init() throws IOException {
        if (eurekaClient != null) {
            receiverThread = new EurekaCheckerThread();
            receiverThread.start();
            processingThreadPool = Executors.newCachedThreadPool(new NamedThreadFactory("Eureka Status Checker"));
        }
    }

    public final void dispose() {
        LOG.debug("dispose called");
        processingThreadPool.shutdownNow();
        stopped = true;
        receiverThread.interrupt();
    }

    private final class EurekaCheckerThread extends Thread {
        private boolean metadataRegistered = false;

        public EurekaCheckerThread() {
            super("Eureka Status Checker Thread");
            setDaemon(true);
        }

        @Override
        public void run() {
            try {
                while (!stopped) {
                    CacheManagerPeerListener cacheManagerPeerListener = cacheManager.getCachePeerListener("RMI");
                    if (cacheManagerPeerListener == null) {
                        LOG.warn("The RMICacheManagerPeerListener is missing. You need to configure a cacheManagerPeerListenerFactory" +
                                " with class=\"net.sf.ehcache.distribution.RMICacheManagerPeerListenerFactory\" in ehcache.xml.");
                    } else {
                        List<InstanceInfo> instanceInfos = eurekaClient.getInstancesByVipAddress(serviceName, false);
                        List<RMICachePeer> localCachePeers = new ArrayList<>();
                        for (Object o : cacheManagerPeerListener.getBoundCachePeers()) {
                            if (o instanceof RMICachePeer)
                                localCachePeers.add((RMICachePeer)o);
                        }
                        if (!metadataRegistered) {
                            Map<String, String> metadata = new HashMap<>();
                            if (localCachePeers.size() > 0) {
                                metadata.put(METADATA_CACHES_KEY, localCachePeers.stream()
                                        .map(rmiCachePeer -> {
                                            try {
                                                return rmiCachePeer.getName();
                                            } catch (RemoteException e) {
                                                return null;
                                            }
                                        })
                                        .collect(Collectors.joining(METADATA_CACHE_DELIMITER)));
                                metadata.put(METADATA_PORT_KEY, localCachePeers.get(0).getUrlBase().split(":")[1]);
                            }
                            appInfoMng.registerAppMetadata(metadata);
                            metadataRegistered = true;
                        }
                        String rmiUrls = instanceInfos.stream()
                                .filter(instanceInfo -> instanceInfo.getStatus() == InstanceInfo.InstanceStatus.UP)
                                .flatMap(instanceInfo -> {
                                    String port = instanceInfo.getMetadata().get(METADATA_PORT_KEY);
                                    String caches = instanceInfo.getMetadata().get(METADATA_CACHES_KEY);
                                    if (!Strings.isNullOrEmpty(caches) && !Strings.isNullOrEmpty(port)) {
                                        return Arrays.stream(caches.split(METADATA_CACHE_DELIMITER_REGEX))
                                                .map(cacheName -> getUrl(instanceInfo.getIPAddr(), port, cacheName));
                                    }
                                    return Stream.empty();
                                })
                                .filter(s -> !self(s))
                                .collect(Collectors.joining("|"));
                        LOG.trace("rmiUrls received {}", rmiUrls);
                        processRmiUrls(rmiUrls);
                    }
                    Thread.sleep(getRefreshInterval());
                }
            } catch (Throwable t) {
                LOG.error("Eureka checker thread caught throwable. Cause was " + t.getMessage() + ". Continuing...");
            }
        }

        /**
         * This method forks a new executor to process the received Eureka status in a thread pool.
         * That way each remote cache manager cannot interfere with others.
         * <p/>
         * In the worst case, we have as many concurrent threads as remote cache managers.
         *
         * @param rmiUrls
         */
        private void processRmiUrls(final String rmiUrls) {
            if (rmiUrlsProcessingQueue.contains(rmiUrls)) {
                LOG.debug("We are already processing these rmiUrls. Another refresh attempt happened before we finished: {}", rmiUrls);
                return;
            }

            if (processingThreadPool == null)
                return;

            processingThreadPool.execute(() -> {
                try {
                    // Add the rmiUrls we are processing.
                    rmiUrlsProcessingQueue.add(rmiUrls);
                    for (StringTokenizer stringTokenizer = new StringTokenizer(rmiUrls, METADATA_CACHE_DELIMITER); stringTokenizer.hasMoreTokens();) {
                        if (stopped)
                            return;
                        String rmiUrl = stringTokenizer.nextToken();
                        registerNotification(rmiUrl);
                        if (!peerProvider.getPeerUrls().containsKey(rmiUrl)) {
                            LOG.debug("Aborting processing of rmiUrls since failed to add rmiUrl: {}", rmiUrl);
                            return;
                        }
                    }
                } finally {
                    // Remove the rmiUrls we just processed
                    rmiUrlsProcessingQueue.remove(rmiUrls);
                }
            });
        }

        /**
         * @param rmiUrl
         * @return true if our own hostname and listener port are found in the list. This then means we have
         *         caught our onw multicast, and should be ignored.
         */
        private boolean self(String rmiUrl) {
            CacheManager cacheManager = peerProvider.getCacheManager();
            CacheManagerPeerListener cacheManagerPeerListener = cacheManager.getCachePeerListener("RMI");
            if (cacheManagerPeerListener == null) {
                return false;
            }
            List boundCachePeers = cacheManagerPeerListener.getBoundCachePeers();
            if (boundCachePeers == null || boundCachePeers.size() == 0) {
                return false;
            }
            CachePeer peer = (CachePeer) boundCachePeers.get(0);
            try {
                String cacheManagerUrlBase = peer.getUrlBase();
                int baseUrlMatch = rmiUrl.indexOf(cacheManagerUrlBase);
                return baseUrlMatch != -1;
            } catch (RemoteException e) {
                LOG.error("Error getting url base", e);
                return false;
            }
        }

        private void registerNotification(String rmiUrl) {
            peerProvider.registerPeer(rmiUrl);
        }
    }
}
