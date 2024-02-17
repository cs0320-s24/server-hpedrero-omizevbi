package edu.brown.cs.student.main.CSVParser;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ProxyCache<T> {

    public final LoadingCache<String, String> cache;

    /**
     * Constructs a new ProxyCache with the specified maximum size, expiration time after write, and time unit.
     *
<<<<<<< Updated upstream
     * @param maxSize         The maximum size of the cache.
     * @param expireAfterWrite The duration after which entries in the cache expire after being written.
     * @param timeUnit        The TimeUnit for specifying the time duration.
=======
     * @param maxSize          The maximum number of entries the cache can hold.
     * @param expireAfterWrite The duration after which entries expire and are eligible for removal.
     * @param timeUnit         The unit of time for the expiration duration.
>>>>>>> Stashed changes
     */
    public ProxyCache(int maxSize, long expireAfterWrite, TimeUnit timeUnit) {
        this.cache = CacheBuilder.newBuilder()
            .maximumSize(maxSize)
            .expireAfterWrite(expireAfterWrite, timeUnit)
            .build(new CacheLoader<>() {
                @Override
                public String load(String key) {
                    throw new UnsupportedOperationException("load operation cannot be directly invoked.");
                }
            });
    }

    /**
     * Retrieves the cached data associated with the specified key, if present.
     *
     * @param key The key whose associated value is to be retrieved.
     * @return The cached data associated with the key, or null if no mapping exists for the key.
     */
    public String getCachedData(String key) {
        return this.cache.getIfPresent(key);
    }

    /**
     * Associates the specified data with the specified key in the cache.
     *
     * @param key  The key with which the specified data is to be associated.
     * @param data The data to be associated with the specified key.
     */
    public void putData(String key, String data) {
        this.cache.put(key, data);
    }
}
