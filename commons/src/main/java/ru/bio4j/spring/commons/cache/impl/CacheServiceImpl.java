package ru.bio4j.spring.commons.cache.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import net.sf.ehcache.config.DiskStoreConfiguration;
import ru.bio4j.spring.commons.cache.CacheEventListener;
import ru.bio4j.spring.commons.cache.CacheService;
import ru.bio4j.spring.commons.cache.util.CacheEventListenerWrapper;
import ru.bio4j.spring.commons.cache.util.CacheUtil;
import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.model.config.props.CacheProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheServiceImpl implements CacheService {
	private static LogWrapper LOG = LogWrapper.getLogger(CacheService.class);

    private final static String CACHE_DEFAULT_CONFIG_FILE = "ehcache-default-config.xml";
	private final static String CACHE_DEFAULT_PERSISTENT_PATH = "ehcache-storage";
	private final static String CACHE_PERSISTENT_PATH_PARAM = "${ehcache.persistent.path}";
	private final static String CACHE_CONFIG_FILE_PARAM = "${ehcache.config}";

	private CacheProperties cacheProperties;

	private final Map<CacheEventListener, CacheEventListenerWrapper> listeners = new ConcurrentHashMap<>();

	public CacheServiceImpl(CacheProperties cacheProperties) {
		this.cacheProperties = cacheProperties;
	}

	@Override
	public <Key extends Serializable, T extends Serializable> void put(String cacheName, Key key, T value) {
		this.put(cacheName, key, value, false);
	}

	@Override
	public <Key extends Serializable, T extends Serializable> void put(String cacheName, Key key1, Key key2, T value) {
		this.put(cacheName, CacheUtil.createKeyFromObjects(key1, key2), value, false);
	}
	
	@Override
	public <Key extends Serializable, T extends Serializable> void put(String cacheName, Key key, T value, boolean notifyListeners) {
		checkForNull(key, value);
		LOG.trace("Attempting to put object {} into cache with key {}", value, key);
		Cache cache = getCache(cacheName);
		cache.put(new Element(key, value), !notifyListeners);
	}
	
	@Override
	public <Key extends Serializable, T extends Serializable> T get(String cacheName, Key key) {
		Cache cache = getCache(cacheName);
		Element element = cache.get(key);
		T result = getValue(element);
		LOG.trace("got from cahe {} by key {}", result, key);
		return result;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <Key extends Serializable> List<Key> getKeys(String cacheName) {
		Cache cache = getCache(cacheName);
        return cache.getKeys();
	}
	
	@Override
	public <Key extends Serializable, T extends Serializable> boolean isKeyInCache(String cacheName, Key key) {
		return getCache(cacheName).isKeyInCache(key);
	}

	@Override
	public <Key extends Serializable, T extends Serializable> T get(String cacheName, Key key1, Key key2) {
		return get(cacheName, CacheUtil.createKeyFromObjects(key1, key2));
		
	}

	@Override
	public <Key extends Serializable, T extends Serializable> T remove(String cacheName, Key key1, Key key2) {
		return remove(cacheName, CacheUtil.createKeyFromObjects(key1, key2));
	}
	
	@Override
	public <Key extends Serializable, T extends Serializable> T remove(String cacheName, Key key) {
		Cache cache = getCache(cacheName);
		Element element = cache.removeAndReturnElement(key);
		T result = getValue(element);
		LOG.trace("removed from cahe {} by key {}", result, key);
		return result;
	}

	@Override
	public void flush(String cacheName) {
		try {
			if (isDiskPersistence(cacheName)) {
				Cache cache = getCache(cacheName);
				cache.flush();
			}
		} catch (Exception e) {
			LOG.error("Ooops.. can't flush cache " + cacheName, e);
		}
	}

	@Override
	public void clear(String cacheName) {
		Cache cache = getCache(cacheName);
		cache.removeAll();
	}

	public void registerListener(String cacheName, CacheEventListener listener) {
		CacheEventListenerWrapper wrapper = listeners.get(listener);
		if (wrapper == null) {
			wrapper = new CacheEventListenerWrapper(listener);
		}
		Cache cache = getCache(cacheName);
		cache.getCacheEventNotificationService().registerListener(wrapper);
	}
	
	public void removeListener(String cacheName, CacheEventListener listener) {
		CacheEventListenerWrapper wrapper = listeners.get(listener);
		if (wrapper == null) {
			return;
		}
		Cache cache = getCache(cacheName);
		cache.getCacheEventNotificationService().unregisterListener(wrapper);
		listeners.remove(listener);
	}

	@Override
	public void removeAllListeners() {
		for (String cacheName : cacheManager.getCacheNames()) {
			Cache cache = getCache(cacheName);
			if (cache != null) {
				cache.getCacheEventNotificationService().getCacheEventListeners().clear();
			}
		}
		listeners.clear();
	}

	private String getCacheConfigFile() {
		String cacheConfigFile = cacheProperties.getCacheConfigFile();
		if(Strings.isNullOrEmpty(cacheConfigFile) || Strings.compare(cacheConfigFile, CACHE_CONFIG_FILE_PARAM, true))
			return CACHE_DEFAULT_CONFIG_FILE;
		return Strings.resourceExists(cacheConfigFile) ? cacheConfigFile : CACHE_DEFAULT_CONFIG_FILE;
	}

	private String getCachePersistentPath() {
		String cachePersistentPath = cacheProperties.getCachePersistentPath();
		if(Strings.isNullOrEmpty(cachePersistentPath) || Strings.compare(cachePersistentPath, CACHE_PERSISTENT_PATH_PARAM, true))
			return CACHE_DEFAULT_PERSISTENT_PATH;
		return cachePersistentPath;
	}

	private boolean isDiskPersistence(String cacheName) {
        Map<String, CacheConfiguration> cacheConfigurations = cacheManager.getConfiguration().getCacheConfigurations();
		return cacheConfigurations.get(cacheName).isDiskPersistent();
	}
	
	@SuppressWarnings("unchecked")
	private <Key extends Serializable, T extends Serializable> T getValue(Element element) {
		if (element != null) {
            return (T) element.getObjectValue();
		}
		return null;
	}
	
	private void checkForNull(Object value1, Object value2) {
		if (value1 == null || value2 == null) {
			throw new IllegalArgumentException("Object cannot be null");
		}
	}
	
	private Cache getCache(String cacheName) throws IllegalArgumentException, IllegalStateException {
		initCach();
		if(cacheManager == null)
			throw new IllegalStateException("CacheManager is not inited!!!");
		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null) {
			throw new IllegalArgumentException("Unknown cache " + cacheName);
		}
		return cache;
	}

	@Override
	public void printTotalStatistics() {
		long inMemSizeInBytes = 0l;
		long onDiskSizeInBytes = 0l;
		long inMemCount = 0;
		int onDiskCount = 0;
		for (String cacheName : cacheManager.getCacheNames()) {
			Cache cache = getCache(cacheName);
			inMemSizeInBytes += cache.calculateInMemorySize();
			inMemCount += cache.getMemoryStoreSize();
			if (isDiskPersistence(cacheName)) {
				onDiskSizeInBytes += cache.calculateOnDiskSize();
				onDiskCount += cache.getDiskStoreSize();
				LOG.info(
						"cacheName {}, number of elements in the disk store is {}, " +
								"size of the on-disk store for this cache in bytes is {}",
						new Object[] { cacheName, cache.getDiskStoreSize(),
								cache.calculateOnDiskSize() });
			}
			LOG.info(
					"cacheName {}, number of elements in the memory store is {}, " +
							"size of the memory store for this cache in bytes is {}",
					new Object[] { cacheName, cache.getMemoryStoreSize(),
							cache.calculateInMemorySize() });
		}
		LOG.info(
				"Total number of elements in the disk store is {}, " +
						"Total size of the on-disk store for this in bytes is {}",
				onDiskCount, onDiskSizeInBytes);
		LOG.info(
				"Total number of elements in the memory store is {}, " +
						"Total size of the memory store for this in bytes is {}",
				inMemCount, inMemSizeInBytes);
	}

    private volatile Configuration serviceConfiguration;
    private void createCacheConfiguration() {
    	LOG.debug("Attempting to find cache configuration");
        try(InputStream configIn = Strings.openResourceAsStream(getCacheConfigFile())) {
			if (configIn == null) {
				LOG.debug("Could not find configuration content for cache service");
				throw new IllegalArgumentException("Could not find cache config content");
			}
			LOG.debug("Attempting to create new cache service");
			serviceConfiguration = ConfigurationFactory.parseConfiguration(configIn);
			DiskStoreConfiguration cfg = null;
				cfg = createDiskStoreConfiguration();
			if (cfg != null)
				serviceConfiguration.diskStore(cfg);
        } catch (IOException e) {
        	LOG.error("Failed to create Configuration!", e);
			throw Utl.wrapErrorAsRuntimeException(e);
		}
    }

    private DiskStoreConfiguration createDiskStoreConfiguration() throws IOException {
        String cachePath = getCachePersistentPath();
        final Path cachePathPath = Paths.get(cachePath);
        Files.createDirectories(cachePathPath);
        LOG.debug("Cache persistent path is {}", cachePath);
        DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
        diskStoreConfiguration.setPath(cachePath);
        return diskStoreConfiguration;
    }

    private CacheManager cacheManager;
	private volatile boolean cacheManagerInited = false;
    private synchronized void initCach() {
    	if(!cacheManagerInited) {
    		LOG.debug("Config is not null. Loading CacheConfiguration...");
			createCacheConfiguration();
			cacheManager = CacheManager.create(serviceConfiguration);
			//InputStream configIn = getClass().getClassLoader().getResourceAsStream(CACHE_CONFIG_FILE);
			//cacheManager = CacheManager.create(configIn);
			cacheManagerInited = true;
		}
	}

    @PostConstruct
    public void doStart() throws Exception {
		LOG.debug("Starting...");

		initCach();

		LOG.debug("Started.");
    }

    @PreDestroy
    public void doStop() throws Exception {
		LOG.debug("Stoping...");
		if(this.cacheManager != null)
			this.cacheManager.shutdown();
		LOG.debug("Stoped.");
    }


}
