package com.cache.factory;

import com.cache.core.Cache;
import com.cache.core.LoadBalancer;
import com.cache.database.Database;
import com.cache.strategy.distribution.*;
import com.cache.strategy.prefetching.*;

public class CacheFactory {
    public static Cache createCache(String prefetchType, String distributionType, Database db) {
        PreFetchingPolicy prefetcher = buildPreFetcher(prefetchType);
        DistributionPolicy distributor = buildDistributor(distributionType);

        LoadBalancer router = new LoadBalancer(distributor);
        return new Cache(router, prefetcher, db);
    }

    private static PreFetchingPolicy buildPreFetcher(String name) {
        if ("simple".equalsIgnoreCase(name)) {
            return new SimplePreFetchingPolicy(8);
        }
        throw new IllegalArgumentException("Unsupported PreFetchingPolicy: " + name);
    }

    private static DistributionPolicy buildDistributor(String name) {
        if ("modulo".equalsIgnoreCase(name)) {
            return new ModuloDistributionPolicy();
        }
        throw new IllegalArgumentException("Unsupported DistributionPolicy: " + name);
    }
}
