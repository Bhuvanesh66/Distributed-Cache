package com.cache.strategy.eviction;

import com.cache.model.Key;
import com.cache.observer.CacheStateObserver;

public interface EvictionPolicy {
    void keyAccessed(Key key);
    Key evictKey();
    void removeKey(Key key);
    void addObserver(CacheStateObserver observer);
}
