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

    /**
     * Constructs a new ProxyCache object with the specified parameters.
     *
     * @param reader          The Reader object for reading data from the data source.
     * @param creator         The CreatorFromRow object for creating type T objects from CSV rows.
     * @param hasHeaders      A boolean indicating whether the CSV data has headers.
     * @param maxSize         The maximum size of the cache.
     * @param expireAfterWrite The duration after which entries in the cache expire after being written.
     * @param timeUnit        The TimeUnit for specifying the time duration.
     * @throws Exception If an error occurs during the construction of the CSV parser or cache.
     */
    public ProxyCache(Reader reader, CreatorFromRow<T> creator, boolean hasHeaders, int maxSize, long expireAfterWrite, TimeUnit timeUnit) throws Exception {
        this.parser = new CSVParser<>(reader, creator, hasHeaders);
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireAfterWrite, timeUnit)
                .build(new CacheLoader<>() {
            @Override
            public List<T> load(String key) throws Exception {
                return parser.getParsed();
            }
        });
    }

    /**
     * Loads data from the cache for the specified path.
     *
     * @param path The path identifying the data to be loaded from the cache.
     * @return A list of objects of type T loaded from the cache.
     * @throws Exception If an error occurs while loading the data from the cache.
     */
    public List<T> loadData(String path) throws Exception {
        return this.cache.get(path);
    }

    /**
     * Searches for the specified query within the data loaded from the cache for the given path.
     * If the data for the path is not present in the cache, it is loaded from the CSV parser and then cached.
     *
     * @param path  The path identifying the data to be searched within the cache.
     * @param query The query string to search for within the cached data.
     * @return A list of objects of type T that match the specified query within the cached data.
     * @throws Exception If an error occurs while searching or loading the data from the cache.
     */
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
