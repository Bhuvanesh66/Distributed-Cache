package com.cache.strategy.prefetching;

import java.util.List;

import com.cache.model.Key;

public interface PreFetchingPolicy {
    void recordAccess(Key key);
    List<Key> getKeysToFetch(Key currentKey);
}
