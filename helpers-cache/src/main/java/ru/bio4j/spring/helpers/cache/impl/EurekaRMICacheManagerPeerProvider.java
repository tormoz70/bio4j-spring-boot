package ru.bio4j.spring.helpers.cache.impl;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClient;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.distribution.CacheManagerPeerProvider;
import net.sf.ehcache.distribution.CachePeer;
import net.sf.ehcache.distribution.RMICacheManagerPeerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.commons.types.ApplicationContextProvider;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.*;

public class EurekaRMICacheManagerPeerProvider extends RMICacheManagerPeerProvider implements CacheManagerPeerProvider {
    private static final Logger LOG = LoggerFactory.getLogger(EurekaRMICacheManagerPeerProvider.class.getName());

    private final EurekaStatusChecker eurekaChecker;

    public EurekaRMICacheManagerPeerProvider(CacheManager cacheManager, String serviceName, long refreshInterval) {
        super(cacheManager);
        EurekaClient client = ApplicationContextProvider.getBean(EurekaClient.class);
        ApplicationInfoManager appInfoMng = ApplicationContextProvider.getBean(ApplicationInfoManager.class);
        this.eurekaChecker = new EurekaStatusChecker(this, serviceName.toLowerCase(Locale.ROOT), refreshInterval, client, appInfoMng, cacheManager);
    }

    /**
     * Gets the cache name out of the url
     * @param rmiUrl
     * @return the cache name as it would appear in ehcache.xml
     */
    static String extractCacheName(String rmiUrl) {
        return rmiUrl.substring(rmiUrl.lastIndexOf('/') + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        try {
            eurekaChecker.init();
        } catch (IOException e) {
            LOG.error("Error starting heartbeat. Error was: " + e.getMessage(), e);
            throw new CacheException(e.getMessage());
        }
    }

    /**
     * Time for a cluster to form. This varies considerably, depending on the implementation.
     *
     * @return the time in ms, for a cluster to form
     */
    @Override
    public long getTimeForClusterToForm() {
        return 30000;
    }

    public Map getPeerUrls() {
        return peerUrls;
    }

    /**
     * Register a new peer, but only if the peer is new, otherwise the last seen timestamp is updated.
     * <p/>
     * This method is thread-safe. It relies on peerUrls being a synchronizedMap
     *
     * @param rmiUrl
     */
    @Override
    public void registerPeer(String rmiUrl) {
        try {
            CachePeerEntry cachePeerEntry = (CachePeerEntry) peerUrls.get(rmiUrl);
            if (cachePeerEntry == null || stale(cachePeerEntry.date)) {
                //can take seconds if there is a problem
                CachePeer cachePeer = lookupRemoteCachePeer(rmiUrl);
                cachePeerEntry = new CachePeerEntry(cachePeer, new Date());
                //synchronized due to peerUrls being a synchronizedMap
                Object prev = peerUrls.put(rmiUrl, cachePeerEntry);
                if (prev == null)
                    LOG.debug("New remote cache peer for {} has registered.", rmiUrl);
            } else {
                cachePeerEntry.date = new Date();
            }
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to lookup remote cache peer for " + rmiUrl + ". Removing from peer list. Cause was: "
                        + e.getMessage());
            }
            unregisterPeer(rmiUrl);
        } catch (NotBoundException e) {
            peerUrls.remove(rmiUrl);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to lookup remote cache peer for " + rmiUrl + ". Removing from peer list. Cause was: "
                        + e.getMessage());
            }
        } catch (Throwable t) {
            LOG.error("Unable to lookup remote cache peer for " + rmiUrl
                    + ". Cause was not due to an IOException or NotBoundException which will occur in normal operation:" +
                    " " + t.getMessage());
        }
    }

    /**
     * @param cache
     * @return a list of {@link CachePeer} peers for the given cache, excluding the local peer.
     */
    @Override
    public final synchronized List listRemoteCachePeers(Ehcache cache) throws CacheException {
        List<CachePeer> remoteCachePeers = new ArrayList<>();
        List<String> staleList = new ArrayList<>();
        synchronized (peerUrls) {
            for (Object o : peerUrls.keySet()) {
                String rmiUrl = (String) o;
                String rmiUrlCacheName = extractCacheName(rmiUrl);
                try {
                    if (!rmiUrlCacheName.equals(cache.getName()))
                        continue;
                    CachePeerEntry cachePeerEntry = (CachePeerEntry) peerUrls.get(rmiUrl);
                    if (!stale(cachePeerEntry.date)) {
                        CachePeer cachePeer = cachePeerEntry.cachePeer;
                        remoteCachePeers.add(cachePeer);
                    } else {
                        LOG.debug("{} is stale. Either the remote peer is shutdown or the " +
                                        "network connectivity has been interrupted. Will be removed from list of remote cache peers",
                                rmiUrl);
                        staleList.add(rmiUrl);
                    }
                } catch (Exception exception) {
                    LOG.error(exception.getMessage(), exception);
                    throw new CacheException("Unable to list remote cache peers. Error was " + exception.getMessage());
                }
            }
            //Must remove entries after we have finished iterating over them
            staleList.forEach(peerUrls::remove);
        }
        return remoteCachePeers;
    }

    /**
     * Shutdown the checker
     */
    public final void dispose() {
        eurekaChecker.dispose();
    }

    /**
     * The time after which an unrefreshed peer provider entry is considered stale.
     */
    protected long getStaleTime() {
        return this.eurekaChecker.getRefreshInterval() * 2 + 100;
    }

    /**
     * Whether the entry should be considered stale. This will depend on the type of RMICacheManagerPeerProvider.
     * <p/>
     *
     * @param date the date the entry was created
     * @return true if stale
     */
    @Override
    protected boolean stale(Date date) {
        long now = System.currentTimeMillis();
        return date.getTime() < (now - getStaleTime());
    }

    /**
     * Entry containing a looked up CachePeer and date
     */
    protected static final class CachePeerEntry {

        private final CachePeer cachePeer;
        private Date date;

        /**
         * Constructor
         *
         * @param cachePeer the cache peer part of this entry
         * @param date      the date part of this entry
         */
        public CachePeerEntry(CachePeer cachePeer, Date date) {
            this.cachePeer = cachePeer;
            this.date = date;
        }
    }
}
