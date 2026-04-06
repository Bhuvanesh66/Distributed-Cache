package com.cache;

import com.cache.core.Cache;
import com.cache.database.Database;
import com.cache.factory.*;
import com.cache.model.KeyImpl;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Distributed Cache System Demo ===");

        Database dataStore = new Database();
        Cache cacheSystem = CacheFactory.createCache("simple", "modulo", dataStore);
        CacheNodeFactory nodeBuilder = new CacheNodeFactory("lru", "active", 100, 60000);
        cacheSystem.addCacheNodes(nodeBuilder.createNodes(3));
        System.out.println("Cache initialized with 3 nodes\n");

        System.out.println("--- Initial cache population ---");
        for (int i = 1; i <= 20; i++) {
            System.out.println("Retrieving key " + i + ": " + cacheSystem.get(new KeyImpl(i)));
        }

        System.out.println("\n--- Adding 2 more nodes to cluster ---");
        long startRebalance = System.currentTimeMillis();
        cacheSystem.addCacheNodes(nodeBuilder.createNodes(2));
        long endRebalance = System.currentTimeMillis();
        System.out.println("Rebalancing completed in " + (endRebalance - startRebalance) + " ms\n");

        try {
            System.out.println("Waiting for prefetch completion...");
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("--- Accessing after cluster rebalance ---");
        for (int i = 1; i <= 26; i++) {
            System.out.println("Retrieving key " + i + ": " + cacheSystem.get(new KeyImpl(i)));
        }

        try {
            System.out.println("\nWaiting for TTL expiration (60 seconds)...");
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("--- Accessing after TTL expiration ---");
        for (int i = 1; i <= 12; i++) {
            System.out.println("Retrieving key " + i + ": " + cacheSystem.get(new KeyImpl(i)));
        }

        cacheSystem.shutdown();
    }
}