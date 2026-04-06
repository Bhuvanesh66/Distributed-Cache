package com.cache.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cache.model.Key;
import com.cache.model.NodeKey;
import com.cache.model.Value;
import com.cache.strategy.distribution.DistributionPolicy;

public class Redistributor {
    public static void redistribute(LoadBalancer balancer) {
        Map<NodeKey, CacheNode> nodeMapping = balancer.getNodeMapSnapshot();
        DistributionPolicy routingPolicy = balancer.getDistributionPolicy();

        for (Map.Entry<NodeKey, CacheNode> nodeEntry : nodeMapping.entrySet()) {
            NodeKey currentNodeId = nodeEntry.getKey();
            CacheNode currentNode = nodeEntry.getValue();
            List<Key> storedKeys = new ArrayList<>(currentNode.getKeys());

            for (Key dataKey : storedKeys) {
                NodeKey assignedNodeId = routingPolicy.getTargetNode(dataKey);

                if (!currentNodeId.equals(assignedNodeId)) {
                    CacheNode targetNode = nodeMapping.get(assignedNodeId);
                    if (targetNode != null) {
                        Value cachedData = currentNode.get(dataKey);
                        targetNode.put(dataKey, cachedData);
                        currentNode.deleteKey(dataKey);
                    }
                }
            }
        }
    }
}