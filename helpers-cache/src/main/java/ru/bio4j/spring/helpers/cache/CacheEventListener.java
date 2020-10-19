package ru.bio4j.spring.helpers.cache;

public interface CacheEventListener {
	
    void notifyElementRemoved(String cacheName, Object value);

    void notifyElementPut(String cacheName, Object value);

    void notifyElementUpdated(String cacheName, Object value);

    void notifyElementExpired(String cacheName, Object value);

    void notifyElementEvicted(String cacheName, Object value);

    void notifyRemoveAll(String cacheName);

}
