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

    private final CSVParser<T> parser;
    public final LoadingCache<String, List<T>> cache;

    public ProxyCache(Reader reader, CreatorFromRow<T> creator, boolean hasHeaders, int maxSize, long expireAfterWrite, TimeUnit timeUnit) throws Exception {
        this.parser = new CSVParser<>(reader, creator, hasHeaders);
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireAfterWrite, timeUnit)
                .build(new CacheLoader<String, List<T>>() {
            @Override
            public List<T> load(String key) throws Exception {
                return parser.getParsed();
            }
        });
    }

    public List<T> loadData(String path) throws Exception {
        return this.cache.get(path);
    }

    public List<T> search(String path, String query) throws Exception {
        List<T> data = this.cache.getIfPresent(path);
        if (data == null) {
            data = parser.getParsed();
            cache.put(path, data);
        }

        List<T> searchResult = new ArrayList<>();
        for (T item : data) {
            if (item.equals(query)) {
                searchResult.add(item);
            }
        }
        return searchResult;
    }
}
