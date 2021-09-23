package ru.bio4j.spring.helpers.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.commons.types.ListSupplier;
import ru.bio4j.spring.commons.types.ObjectSupplier;
import ru.bio4j.spring.model.transport.ABean;
import ru.bio4j.spring.model.transport.BeansPage;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
 * Адаптер для доступа к кэшу
 */
public class CacheHelper {
    private static final Logger LOG = LoggerFactory.getLogger(CacheHelper.class);
    private final CacheService cacheService;

    public CacheHelper(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public String getRequestHash(HttpServletRequest request){
        return request.getRequestURI() + "?" + request.getQueryString();
    }

    public <T extends Serializable> T getObjectFromCache(String cacheName, String key) {
        if(cacheService == null)
            throw new IllegalArgumentException("cacheService not defined!");
        T res = cacheService.get(cacheName, key);
        if (res != null)
            LOG.debug("Cache hit! [name: {}, key: {}]", cacheName, key);
        return res;
    }
    public <T extends Serializable> T getObjectFromCache(String cacheName, HttpServletRequest request) {
        String requestHash = getRequestHash(request);
        return getObjectFromCache(cacheName, requestHash);
    }

    public <T extends Serializable> void putObjectToCache(String cacheName, String key, T value) {
        if(cacheService == null)
            throw new IllegalArgumentException("cacheService not defined!");
        cacheService.put(cacheName, key, value);
    }

    public <T extends Serializable> T removeObjectFromCache(String cacheName, String key) {
        if(cacheService == null)
            throw new IllegalArgumentException("cacheService not defined!");
        return cacheService.remove(cacheName, key);
    }

    public <T extends Serializable> void putObjectToCache(String cacheName, HttpServletRequest request, T value) {
        String key = getRequestHash(request);
        putObjectToCache(cacheName, key, value);
    }

    public void removeObjectFromCache(String cacheName, HttpServletRequest request) {
        String key = getRequestHash(request);
        removeObjectFromCache(cacheName, key);
    }

    private static final String CACHE_CONTENT_HOLDER = "cached_content_holder";
    public <T extends Serializable> List<T> getListFromCache(String cacheName, String key) {
        if(cacheService == null)
            throw new IllegalArgumentException("cacheService not defined!");
        ABean contaiter = cacheService.get(cacheName, key);
        Object content = contaiter != null ? contaiter.get(CACHE_CONTENT_HOLDER) : null;
        List<T> res =  content != null ? (List<T>)contaiter.get(CACHE_CONTENT_HOLDER) : null;
        if (res != null)
            LOG.debug("Cache hit! [name: {}, key: {}]", cacheName, key);
        return res;
    }
    public <T extends Serializable> List<T> getListFromCache(String cacheName, HttpServletRequest request) {
        String key = getRequestHash(request);
        return getListFromCache(cacheName, key);
    }

    public <T extends Serializable> void putListToCache(String cacheName, String key, List<T> value) {
        if(cacheService == null)
            throw new IllegalArgumentException("cacheService not defined!");
        ABean container = new ABean();
        container.put(CACHE_CONTENT_HOLDER, value);
        cacheService.put(cacheName, key, container);
    }
    public <T extends Serializable> void putListToCache(String cacheName, HttpServletRequest request, List<T> value) {
        String key = getRequestHash(request);
        putListToCache(cacheName, key, value);
    }

    public <T extends Serializable> BeansPage<T> getBeansPageFromCache(String cacheName, String key) {
        if(cacheService == null)
            throw new IllegalArgumentException("cacheService not defined!");
        ABean contaiter = cacheService.get(cacheName, key);
        Object content = contaiter != null ? contaiter.get(CACHE_CONTENT_HOLDER) : null;
        return content != null ? (BeansPage<T>)contaiter.get(CACHE_CONTENT_HOLDER) : null;
    }
    public <T extends Serializable> BeansPage<T> getBeansPageFromCache(String cacheName, HttpServletRequest request) {
        String key = getRequestHash(request);
        return getBeansPageFromCache(cacheName, key);
    }

    public <T extends Serializable> void putBeansPageToCache(String cacheName, String key, BeansPage<T> value) {
        if(cacheService == null)
            throw new IllegalArgumentException("cacheService not defined!");
        ABean container = new ABean();
        container.put(CACHE_CONTENT_HOLDER, value);
        cacheService.put(cacheName, key, container);
    }
    public <T extends Serializable> void putBeansPageToCache(String cacheName, HttpServletRequest request, BeansPage<T> value) {
        String key = getRequestHash(request);
        putBeansPageToCache(cacheName, key, value);
    }

    /**
     * Пытается получить объект из кэша, а если его там нет, то создаёт новый посредством вызова {@code creator} и помещает его в кэш.
     * Если {@code creator} возвращает null, то он не помещается в кэш.
     * @param cacheName имя используемого кэша
     * @param request   объект запроса, который используется для получения уникального ключа
     * @param creator   метод создания нового экзмпляра объекта или {@code null}
     * @param <T>       тип получаемого объекта
     * @return Экземпляр объекта из кэша, либо объект, созданный {@code creator}'ом.
     */
    public <T extends Serializable> T wrapObjectCacheCall(String cacheName, HttpServletRequest request, ObjectSupplier<T> creator) {
        T rslt = getObjectFromCache(cacheName, request);
        if (rslt == null && creator != null) {
            rslt = creator.get();
            if (rslt != null)
                putObjectToCache(cacheName, request, rslt);
        }
        return rslt;
    }
    /**
     * Пытается получить объект из кэша, а если его там нет, то создаёт новый посредством вызова {@code creator} и помещает его в кэш.
     * Если {@code creator} возвращает null, то он не помещается в кэш.
     * @param cacheName имя используемого кэша
     * @param key       уникальный ключ в кэше
     * @param creator   метод создания нового экзмпляра объекта или {@code null}
     * @param <T>       тип получаемого объекта
     * @return Экземпляр объекта из кэша, либо объект, созданный {@code creator}'ом.
     */
    public <T extends Serializable> T wrapObjectCacheCall(String cacheName, String key, ObjectSupplier<T> creator) {
        T rslt = getObjectFromCache(cacheName, key);
        if (rslt == null && creator != null) {
            rslt = creator.get();
            if (rslt != null)
                putObjectToCache(cacheName, key, rslt);
        }
        return rslt;
    }

    /**
     * Пытается получить список объектов из кэша, а если его там нет, то создаёт новый посредством вызова {@code creator} и помещает его в кэш.
     * Если {@code creator} возвращает null, то он не помещается в кэш.
     * @param cacheName имя используемого кэша
     * @param request   объект запроса, который используется для получения уникального ключа
     * @param creator   метод создания нового списка объектов или {@code null}
     * @param <T>       тип получаемого объекта в списке
     * @return Список объектов из кэша, либо список объектов, созданный {@code creator}'ом.
     */
    public <T extends Serializable> List<T> wrapListCacheCall(String cacheName, HttpServletRequest request, ListSupplier<T> creator) {
        List<T> rslt = getListFromCache(cacheName, request);
        if (rslt == null && creator != null) {
            rslt = creator.get();
            if (rslt != null)
                putListToCache(cacheName, request, rslt);
        }
        return rslt;
    }
    /**
     * Пытается получить список объектов из кэша, а если его там нет, то создаёт новый посредством вызова {@code creator} и помещает его в кэш.
     * Если {@code creator} возвращает null, то он не помещается в кэш.
     * @param cacheName имя используемого кэша
     * @param key       уникальный ключ в кэше
     * @param creator   метод создания нового списка объектов или {@code null}
     * @param <T>       тип получаемого объекта в списке
     * @return Список объектов из кэша, либо список объектов, созданный {@code creator}'ом.
     */
    public <T extends Serializable> List<T> wrapListCacheCall(String cacheName, String key, ListSupplier<T> creator) {
        List<T> rslt = getListFromCache(cacheName, key);
        if (rslt == null && creator != null) {
            rslt = creator.get();
            if (rslt != null)
                putListToCache(cacheName, key, rslt);
        }
        return rslt;
    }
}
