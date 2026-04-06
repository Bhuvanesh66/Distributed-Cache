package com.cache.core;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cache.model.CacheValue;
import com.cache.model.Key;
import com.cache.model.Value;
import com.cache.observer.CacheStateObserver;
import com.cache.strategy.eviction.EvictionPolicy;
import com.cache.strategy.timetolive.TTLPolicy;

public class CacheNode implements CacheStateObserver {

    private final Map<Key, CacheValue> storage;
    private final EvictionPolicy evictionStrategy;
    private final TTLPolicy expirationStrategy;
    private final int maxCapacity;

    public CacheNode(EvictionPolicy eviction, TTLPolicy expiration, int maxSize) {
        this.storage = new ConcurrentHashMap<>();
        this.evictionStrategy = eviction;
        this.expirationStrategy = expiration;
        this.maxCapacity = Math.max(maxSize, 10);
    }

    public Value get(Key key) {
        CacheValue entry = storage.get(key);
        if (entry == null) {
            return null;
        }

        if (expirationStrategy.isExpired(entry)) {
            removeEntry(key);
            return null;
        }

        evictionStrategy.keyAccessed(key);
        return entry.getValue();
    }

    public void put(Key key, Value value) {
        synchronized (this) {
            boolean isNewEntry = !storage.containsKey(key);
            if (isNewEntry && storage.size() >= maxCapacity) {
                evictionStrategy.evictKey();
            }

            storage.put(key, new CacheValue(value));
            evictionStrategy.keyAccessed(key);
        }
    }

    public void deleteKey(Key key) {
        removeEntry(key);
    }

    private void removeEntry(Key key) {
        storage.remove(key);
        evictionStrategy.removeKey(key);
    }

    @Override
    public void onEviction(Key key) {
        removeEntry(key);
    }

    public List<Key> getKeys() {
        return Arrays.asList(storage.keySet().toArray(new Key[0]));
    }
}