package com.cache.strategy.timetolive;

import com.cache.core.CacheNode;
import com.cache.model.Key;
import com.cache.model.CacheValue;

public interface TTLPolicy {
    boolean isExpired(CacheValue cacheValue);
    void initCleanup(Key key, CacheNode node);
}

