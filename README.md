# Distributed Cache

A Java-based low-level design project that simulates a distributed in-memory caching system. The system demonstrates how cache clusters operate by distributing data across nodes, handling cache misses via a backing database, and implementing production-like caching strategies such as LRU eviction, TTL expiration, and asynchronous prefetching.

This project is intended as a learning-oriented system design implementation, showcasing how modular cache components can be composed and coordinated.

---

## Overview

The system models a distributed cache with the following characteristics:

- Horizontal scalability via multiple cache nodes
- Pluggable strategies for distribution, eviction, TTL, and prefetching
- Fault-tolerant data routing using a load balancer
- Dynamic rebalancing when nodes are added or removed

It is not a production-ready cache, but a conceptual and extensible simulation of one.

---

## Architecture

### High-Level Flow
1. A client requests data via Cache.get(key)
2. The LoadBalancer selects the appropriate node using a DistributionPolicy
3. The system checks if the key exists in the selected CacheNode:
    - Hit → Return cached value
    - Miss → Fetch from Database, store in node, then return
4. After access, a PreFetchingPolicy asynchronously loads related keys
5. Each node enforces:
    - Capacity constraints via EvictionPolicy
    - Expiry via TTLPolicy
6. On topology changes:
    - LoadBalancer updates shard mapping
    - Redistributor migrates keys to correct nodes

---

## Default Configuration

The demo setup uses:

| Component | Strategy |
|-----------|------------------------------|
| Distribution |	Modulo-based hashing |
| Eviction |	LRU (Least Recently Used) |
| TTL |	Active expiration |
| Prefetching |	Simple range-based prediction |
| Database |	In-memory with artificial delay |

---

## Key Components

### Core
- Cache

    Entry point for all operations. Coordinates node access, database fallback, and prefetching.

- CacheNode

    Represents an individual cache shard with local storage and policies.
- LoadBalancer

    Routes keys to nodes using a distribution strategy.
- Redistributor

    Handles data movement during node addition/removal.

### Strategies (Pluggable Design)
- DistributionPolicy

    Determines how keys are mapped to nodes
    (e.g., modulo hashing)

- EvictionPolicy

    Manages memory constraints
    (e.g., LRU eviction)

- TTLPolicy

    Controls expiration of cache entries

- PreFetchingPolicy

    Predicts and loads related keys asynchronously

### Supporting Components
- Database

    Simulated backing store with artificial latency to highlight cache benefits

- Factories

    - CacheFactory
    - CacheNodeFactory

    Used to assemble system components without hardcoding implementations

- Models

    Encapsulate key/value abstractions and cache metadata
- Observer
    - CacheStateObserver
    Enables monitoring of cache behavior

---

## Low-Level Design (UML)

![Distributed Cache UML](https://lh3.googleusercontent.com/d/12LvGqZIOhTozumbmDtq6MBhbZW11pS4F)

---

## Project Structure
```
src/main/java/com/cache/
├── Main.java                              // Application entry point, bootstraps and runs demo
├── core/
│   ├── Cache.java                         // Orchestrates cache operations (get, miss handling, prefetch trigger)
│   ├── CacheNode.java                     // Individual cache shard (storage + eviction + TTL enforcement)
│   ├── LoadBalancer.java                  // Key routing logic using distribution policy
│   └── Redistributor.java                 // Data rebalancing when nodes are added/removed
├── database/
│   └── Database.java                      // Simulated backing store with artificial latency
├── factory/
│   ├── CacheFactory.java                  // Builds and configures Cache with selected strategies
│   └── CacheNodeFactory.java              // Creates cache nodes with policies attached
├── model/
│   ├── CacheValue.java                    // Wrapper for stored value with metadata (e.g., timestamps)
│   ├── Key.java                           // Key abstraction interface
│   ├── KeyImpl.java                       // Concrete key implementation
│   ├── NodeKey.java                       // Composite key used for node-level identification
│   ├── Value.java                         // Value abstraction interface
│   └── ValueImpl.java                     // Concrete value implementation
├── observer/
│   └── CacheStateObserver.java            // Observer for monitoring cache events/state changes
└── strategy/
    ├── distribution/
    │   ├── DistributionPolicy.java        // Interface for key-to-node mapping logic
    │   └── ModuloDistributionPolicy.java  // Modulo-based sharding strategy
    ├── eviction/
    │   ├── EvictionPolicy.java            // Interface for cache eviction logic
    │   └── LRUEvictionPolicy.java         // Least Recently Used eviction implementation
    ├── prefetching/
    │   ├── PreFetchingPolicy.java         // Interface for prefetching strategy
    │   └── SimplePreFetchingPolicy.java   // Range-based key prediction and async prefetch
    └── timetolive/
        ├── ActiveTTLPolicy.java           // Actively removes expired entries
        └── TTLPolicy.java                 // Interface for TTL/expiration logic
```

---

## 🛠️ Compilation & Execution

### **1. Compile**
```bash
javac -d out --source-path src/main/java src/main/java/com/cache/Main.java
```

### **2. Run**
```bash
java -cp out com.cache.Main
```

> **Note:**  
> The `-d out` flag keeps compiled `.class` files separate from source code.

---

## Features
- Distributed key routing across multiple cache nodes
- Configurable caching strategies via clean interfaces
- TTL-based automatic expiration of entries
- Asynchronous prefetching to improve cache hit rates
- Dynamic redistribution of data during cluster changes
- Simulated database latency to demonstrate cache effectiveness