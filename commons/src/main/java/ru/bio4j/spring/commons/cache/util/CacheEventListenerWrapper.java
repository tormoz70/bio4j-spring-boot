package ru.bio4j.spring.commons.cache.util;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;


public class CacheEventListenerWrapper implements CacheEventListener {

	ru.bio4j.spring.commons.cache.CacheEventListener listener;
	
	public CacheEventListenerWrapper(ru.bio4j.spring.commons.cache.CacheEventListener listener) {
		this.listener = listener;
	}

	@Override
	public void notifyElementRemoved(Ehcache cache, Element element)
			throws CacheException {
		listener.notifyElementRemoved(cache.getName(), element.getObjectValue());
	}

	@Override
	public void notifyElementPut(Ehcache cache, Element element)
			throws CacheException {
		listener.notifyElementPut(cache.getName(), element.getObjectValue());
	}

	@Override
	public void notifyElementUpdated(Ehcache cache, Element element)
			throws CacheException {
		listener.notifyElementUpdated(cache.getName(), element.getObjectValue());
	}

	@Override
	public void notifyElementExpired(Ehcache cache, Element element) {
		listener.notifyElementExpired(cache.getName(), element.getObjectValue());
	}

	@Override
	public void notifyElementEvicted(Ehcache cache, Element element) {
		listener.notifyElementEvicted(cache.getName(), element.getObjectValue());
	}

	@Override
	public void notifyRemoveAll(Ehcache cache) {
		listener.notifyRemoveAll(cache.getName());
	}

	@Override
	public void dispose() {
		
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
