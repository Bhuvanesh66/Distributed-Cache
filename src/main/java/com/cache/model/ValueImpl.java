package com.cache.model;

public class ValueImpl implements Value {
    private final String data;

    public ValueImpl(String data) {
        this.data = data;
    }

    public String getValue() {
        return data;
    }

    @Override
    public String toString() {
        return data;
    }
}
