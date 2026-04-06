package com.cache.factory;

import java.util.ArrayList;
import java.util.List;

import com.cache.core.CacheNode;
import com.cache.strategy.eviction.*;
import com.cache.strategy.timetolive.*;

public class CacheNodeFactory {
    private final String evictionMethod;
    private final String expirationMethod;
    private final int nodeSize;
    private final long ttlMs;

    public CacheNodeFactory(String eviction, String expiration, int size, long ttl) {
        this.evictionMethod = eviction;
        this.expirationMethod = expiration;
        this.nodeSize = size;
        this.ttlMs = ttl;
    }

    public CacheNodeFactory(String eviction, String expiration, int size) {
        this(eviction, expiration, size, 60000);
    }

    private EvictionPolicy buildEvictioner(String type) {
        if ("lru".equalsIgnoreCase(type)) {
            return new LRUEvictionPolicy(nodeSize);
        }
        throw new IllegalArgumentException("Unknown EvictionPolicy: " + type);
    }

    private TTLPolicy buildExpirer(String type) {
        if ("active".equalsIgnoreCase(type)) {
            return new ActiveTTLPolicy(ttlMs);
        }
        throw new IllegalArgumentException("Unknown TTLPolicy: " + type);
    }

    public List<CacheNode> createNodes(int count) {
        List<CacheNode> nodeList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            EvictionPolicy evitioner = buildEvictioner(evictionMethod);
            TTLPolicy expirer = buildExpirer(expirationMethod);
            CacheNode node = new CacheNode(evitioner, expirer, nodeSize);
            evitioner.addObserver(node);
            nodeList.add(node);
        }

        return nodeList;
    }
}