package com.cache.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import com.cache.model.Key;
import com.cache.model.NodeKey;
import com.cache.model.Value;
import com.cache.strategy.distribution.DistributionPolicy;

public class LoadBalancer {

    private final ConcurrentHashMap<NodeKey, CacheNode> nodeRegistry;
    private final CopyOnWriteArrayList<NodeKey> activeNodeList;
    private final DistributionPolicy routingStrategy;
    private final ReadWriteLock accessLock = new ReentrantReadWriteLock();

    public LoadBalancer(DistributionPolicy strategy) {
        this.nodeRegistry = new ConcurrentHashMap<>();
        this.activeNodeList = new CopyOnWriteArrayList<>();
        this.routingStrategy = strategy;
    }

    public Value get(Key key) {
        accessLock.readLock().lock();
        try {
            CacheNode targetNode = resolveNodeForKey(key);
            return targetNode.get(key);
        } finally {
            accessLock.readLock().unlock();
        }
    }

    public void put(Key key, Value value) {
        accessLock.readLock().lock();
        try {
            CacheNode targetNode = resolveNodeForKey(key);
            targetNode.put(key, value);
        } finally {
            accessLock.readLock().unlock();
        }
    }

    public void delete(Key key) {
        accessLock.readLock().lock();
        try {
            NodeKey nodeKey = routingStrategy.getTargetNode(key);
            CacheNode node = nodeRegistry.get(nodeKey);
            if (node != null) {
                node.deleteKey(key);
            }
        } finally {
            accessLock.readLock().unlock();
        }
    }

    private CacheNode resolveNodeForKey(Key key) {
        NodeKey nodeIdentifier = routingStrategy.getTargetNode(key);
        if (nodeIdentifier == null) {
            throw new IllegalStateException("Node registry is empty");
        }
        CacheNode retrieved = nodeRegistry.get(nodeIdentifier);
        if (retrieved == null) {
            throw new IllegalStateException("Node not found with ID: " + nodeIdentifier);
        }
        return retrieved;
    }

    public Map<NodeKey, CacheNode> getNodeMapSnapshot() {
        return new HashMap<>(nodeRegistry);
    }

    public List<NodeKey> getNodeKeysSnapshot() {
        return new ArrayList<>(activeNodeList);
    }

    public DistributionPolicy getDistributionPolicy() {
        return routingStrategy;
    }

    public void addNodes(List<CacheNode> newNodes) {
        accessLock.writeLock().lock();
        try {
            int currentSize = activeNodeList.size();

            for (int i = 0; i < newNodes.size(); i++) {
                NodeKey id = new NodeKey("Node-" + (currentSize + i + 1));
                nodeRegistry.put(id, newNodes.get(i));
                activeNodeList.add(id);
            }

            routingStrategy.refreshNodes(activeNodeList);

        } finally {
            accessLock.writeLock().unlock();
        }

        Redistributor.redistribute(this);
    }

    public void removeNode(NodeKey nodeId) {
        accessLock.writeLock().lock();

        try {
            nodeRegistry.remove(nodeId);
            activeNodeList.remove(nodeId);
            routingStrategy.refreshNodes(activeNodeList);

        } finally {
            accessLock.writeLock().unlock();
        }
        Redistributor.redistribute(this);
    }
}