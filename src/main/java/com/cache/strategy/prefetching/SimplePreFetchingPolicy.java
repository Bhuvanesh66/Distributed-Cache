package com.cache.strategy.prefetching;

import java.util.*;

import com.cache.model.Key;
import com.cache.model.KeyImpl;

public class SimplePreFetchingPolicy implements PreFetchingPolicy {

    private final int prefetchRadius;

    public SimplePreFetchingPolicy(int radius) {
        this.prefetchRadius = radius;
    }

    @Override
    public void recordAccess(Key key) {
        // Pattern tracking: passive observation
    }

    @Override
    public List<Key> getKeysToFetch(Key currentKey) {
        List<Key> candidates = new ArrayList<>();
        int baseKeyValue = ((KeyImpl) currentKey).getKey();

        for (int offset = -prefetchRadius; offset <= prefetchRadius; offset++) {
            if (offset == 0) {
                continue;
            }
            candidates.add(new KeyImpl(baseKeyValue + offset));
        }

        return candidates;
    }
}