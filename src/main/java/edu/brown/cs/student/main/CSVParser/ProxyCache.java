package edu.brown.cs.student.main.CSVParser;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.CSVParser.Creators.CreatorFromRow;

public class ProxyCache<T> {

    public final LoadingCache<String, String> cache;

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
