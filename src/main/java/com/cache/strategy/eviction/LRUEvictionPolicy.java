package com.cache.strategy.eviction;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.cache.model.Key;
import com.cache.observer.CacheStateObserver;

public class LRUEvictionPolicy implements EvictionPolicy {

    private final DoublyLinkedLRUTracker tracker;
    private final List<CacheStateObserver> listeners;
    private final int maxSize;

    public LRUEvictionPolicy(int maxSize) {
        this.maxSize = maxSize;
        this.listeners = new ArrayList<>();
        this.tracker = new DoublyLinkedLRUTracker();
    }

    @Override
    public synchronized void keyAccessed(Key key) {
        tracker.recordAccess(key);
    }

    @Override
    public void addObserver(CacheStateObserver observer) {
        this.listeners.add(observer);
    }

    @Override
    public synchronized Key evictKey() {
        if (tracker.getSize() <= maxSize) {
            return null;
        }

        Key lruKey = tracker.getOldestKey();
        if (lruKey == null) {
            return null;
        }

        tracker.delete(lruKey);
        notifyEviction(lruKey);

        return lruKey;
    }

    @Override
    public synchronized void removeKey(Key key) {
        tracker.delete(key);
    }

    private void notifyEviction(Key key) {
        for (CacheStateObserver listener : listeners) {
            listener.onEviction(key);
        }
    }
}

class DoublyLinkedLRUTracker {

    private class LinkedNode {
        Key keyData;
        LinkedNode previous;
        LinkedNode subsequent;

        LinkedNode(Key key) {
            this.keyData = key;
        }
    }

    private final Map<Key, LinkedNode> lookup;
    private LinkedNode oldest;
    private LinkedNode newest;

    public DoublyLinkedLRUTracker() {
        this.lookup = new HashMap<>();
    }

    public int getSize() {
        return lookup.size();
    }

    public Key getOldestKey() {
        return oldest != null ? oldest.keyData : null;
    }

    public void recordAccess(Key key) {
        if (lookup.containsKey(key)) {
            LinkedNode existing = lookup.get(key);
            promoteToNewest(existing);
            return;
        }

        LinkedNode newEntry = new LinkedNode(key);
        lookup.put(key, newEntry);
        attachToNewest(newEntry);
    }

    public void delete(Key key) {
        LinkedNode node = lookup.get(key);
        if (node == null) {
            return;
        }

        detachNode(node);
        lookup.remove(key);
    }

    private void promoteToNewest(LinkedNode node) {
        if (node == newest) {
            return;
        }

        detachNode(node);
        attachToNewest(node);
    }

    private void attachToNewest(LinkedNode node) {
        if (newest == null) {
            oldest = newest = node;
            return;
        }

        newest.subsequent = node;
        node.previous = newest;
        newest = node;
    }

    private void detachNode(LinkedNode node) {
        if (node.previous != null) {
            node.previous.subsequent = node.subsequent;
        } else {
            oldest = node.subsequent;
        }

        if (node.subsequent != null) {
            node.subsequent.previous = node.previous;
        } else {
            newest = node.previous;
        }

        node.previous = node.subsequent = null;
    }
}