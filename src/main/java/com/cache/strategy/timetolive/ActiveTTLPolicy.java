package com.cache.strategy.timetolive;

import java.util.concurrent.*;

import com.cache.core.CacheNode;
import com.cache.model.CacheValue;
import com.cache.model.Key;

public class ActiveTTLPolicy implements TTLPolicy {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final long ttlDurationMs;

    public ActiveTTLPolicy(long ttlDurationMs) {
        this.ttlDurationMs = ttlDurationMs;
    }

    @Override
    public boolean isExpired(CacheValue cacheEntry) {
        long elapsedTime = System.currentTimeMillis() - cacheEntry.getCreatedAt();
        return elapsedTime > ttlDurationMs;
    }

    @Override
    public void initCleanup(Key key, CacheNode node) {
        scheduler.schedule(
                () -> {
                    try {
                        node.deleteKey(key);
                    } catch (Exception e) {
                        // Cleanup error: log and continue
                    }
                },
                ttlDurationMs,
                TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}