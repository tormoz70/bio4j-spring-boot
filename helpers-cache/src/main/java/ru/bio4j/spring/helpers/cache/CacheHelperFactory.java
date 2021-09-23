package ru.bio4j.spring.helpers.cache;

/**
 * Фабрика для создания CacheHelper
 */
public class CacheHelperFactory {
    private final CacheService cacheService;

    public CacheHelperFactory(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public CacheHelper create() {
        return new CacheHelper(cacheService);
    }
}
