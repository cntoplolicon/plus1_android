package swj.swj.common;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by cntoplolicon on 9/25/15.
 */
public class LRUCacheMap<K, V> extends LinkedHashMap<K, V> {

    private int capacity;

    public LRUCacheMap(int capacity) {
        super();
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}