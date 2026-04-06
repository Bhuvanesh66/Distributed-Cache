package com.cache.core;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cache.database.Database;
import com.cache.model.Key;
import com.cache.model.Value;
import com.cache.strategy.prefetching.PreFetchingPolicy;
import com.cache.model.NodeKey;

public class Cache {
    private final LoadBalancer loadBalancer;
    private final PreFetchingPolicy preFetchingPolicy;
    private final ExecutorService threadPool;
    private final Database backingStore;

    public Cache(LoadBalancer lb, PreFetchingPolicy pf, Database store) {
        this.loadBalancer = lb;
        this.preFetchingPolicy = pf;
        this.threadPool = Executors.newFixedThreadPool(4);
        this.backingStore = store;
    }

    public Value get(Key key) {
        long startTime = System.currentTimeMillis();
        Value cachedValue = loadBalancer.get(key);

        if (cachedValue == null) {
            cachedValue = retrieveFromDatabase(key);
            if (cachedValue != null) {
                loadBalancer.put(key, cachedValue);
            }
        }

        triggerPrefetch(key);
        logAccessTime(startTime);

        return cachedValue;
    }

    private void triggerPrefetch(Key key) {
        preFetchingPolicy.recordAccess(key);
        List<Key> keysToLoad = preFetchingPolicy.getKeysToFetch(key);
        asyncLoadKeys(keysToLoad);
    }

    private void asyncLoadKeys(List<Key> keys) {
        if (keys == null || keys.isEmpty())
            return;

        threadPool.execute(() -> {
            for (Key k : keys) {
                try {
                    Value existing = loadBalancer.get(k);
                    if (existing == null) {
                        Value retrieved = retrieveFromDatabase(k);
                        if (retrieved != null) {
                            loadBalancer.put(k, retrieved);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Prefetch error for key " + k + ": " + e.getMessage());
                }
            }
        });
    }

    private Value retrieveFromDatabase(Key key) {
        return backingStore.get(key);
    }

    private void logAccessTime(long startTime) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Request completed in " + elapsedTime + "ms");
    }

    public void delete(Key key) {
        loadBalancer.delete(key);
    }

    public void shutdown() {
        threadPool.shutdown();
    }

    public void addCacheNodes(List<CacheNode> nodes) {
        loadBalancer.addNodes(nodes);
    }

    public List<NodeKey> getCacheNodeKeys() {
        return loadBalancer.getNodeKeysSnapshot();
    }

    public void removeCacheNode(NodeKey nodeKey) {
        loadBalancer.removeNode(nodeKey);
    }
}