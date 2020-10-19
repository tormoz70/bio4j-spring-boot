package ru.bio4j.spring.helpers.cache;

import java.io.Serializable;
import java.util.List;

public interface CacheService {

	<Key extends Serializable, T extends Serializable> void put(String cacheName, Key key, T value);
	<Key extends Serializable, T extends Serializable> void put(String cacheName, Key key, T value, boolean notifyListeners);
	 //for not generating garbage (varagrs)
	<Key extends Serializable, T extends Serializable> void put(String cacheName, Key key1, Key key2, T value);
	<Key extends Serializable, T extends Serializable> T get(String cacheName, Key key1, Key key2);
	<Key extends Serializable, T extends Serializable> T remove(String cacheName, Key key1, Key key2);
	
	<Key extends Serializable, T extends Serializable> T get(String cacheName, Key objectKey);
	<Key extends Serializable> List<Key> getKeys(String cacheName);
	<Key extends Serializable, T extends Serializable> T remove(String cacheName, Key objectKey);
	
	//works only with references which upload on start and don't changes!
	<Key extends Serializable, T extends Serializable> boolean isKeyInCache(String cacheName, Key objectKey); //for not generating garbage
	
	void printTotalStatistics();
	void flush(String cacheName);
	void clear(String cacheName);
	void registerListener(String cacheName, CacheEventListener listener);
	void removeListener(String cacheName, CacheEventListener listener);
	void removeAllListeners();
}
