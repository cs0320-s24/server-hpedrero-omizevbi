package edu.brown.cs.student.main.CSVParser;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ProxyCache<T> {

    public final LoadingCache<String, String> cache;
    /**
     * Constructs a new ProxyCache object with the specified parameters.
     *
     * @param maxSize         The maximum size of the cache.
     * @param expireAfterWrite The duration after which entries in the cache expire after being written.
     * @param timeUnit        The TimeUnit for specifying the time duration.
     * @throws Exception If an error occurs during the construction of the CSV parser or cache.
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

    public String getCachedData(String key) {
        return this.cache.getIfPresent(key);
    }

    public void putData(String key, String data) {
        this.cache.put(key, data);
    }
}
