package com.cache.model;

public class KeyImpl implements Key {
    private final int keyValue;

    public KeyImpl(int keyValue) {
        this.keyValue = keyValue;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(keyValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || !(obj instanceof KeyImpl)) {
            return false;
        }

        KeyImpl other = (KeyImpl) obj;
        return keyValue == other.keyValue;
    }

    public int getKey() {
        return keyValue;
    }

    @Override
    public String toString() {
        return "Key[" + keyValue + "]";
    }
}
