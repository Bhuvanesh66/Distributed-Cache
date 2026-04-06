package com.cache.database;

import java.util.Map;
import java.util.HashMap;

import com.cache.model.Key;
import com.cache.model.KeyImpl;
import com.cache.model.Value;
import com.cache.model.ValueImpl;

public class Database {
    Map<Key, Value> storage;

    public Database() {
        storage = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            Key key = new KeyImpl(i);
            Value value = new ValueImpl("value_" + i);
            storage.put(key, value);
        }
    }

    public Value get(Key key) {
        try {
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return storage.get(key);
    }
}
