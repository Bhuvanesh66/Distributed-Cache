package com.cache.observer;

import com.cache.model.Key;

public interface CacheStateObserver {
    void onEviction(Key key);
}
