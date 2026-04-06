package com.cache.model;

import java.util.Objects;

public class NodeKey {
    private final String id;

    public NodeKey(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeKey)) return false;
        NodeKey that = (NodeKey) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}