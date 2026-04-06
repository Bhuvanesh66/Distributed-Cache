package com.cache.model;

public class CacheValue {
    private final Value storedValue;
    private final long timestamp;
    private long lastAccessTime;

    public CacheValue(Value data) {
        this.storedValue = data;
        this.timestamp = System.currentTimeMillis();
        this.lastAccessTime = this.timestamp;
    }

    public void refreshAccessTime() {
        this.lastAccessTime = System.currentTimeMillis();
    }

    public Value getValue() {
        return storedValue;
    }

    public long getCreatedAt() {
        return timestamp;
    }

    public long getLastAccessedAt() {
        return lastAccessTime;
    }

    public long getAge() {
        return System.currentTimeMillis() - timestamp;
    }
}