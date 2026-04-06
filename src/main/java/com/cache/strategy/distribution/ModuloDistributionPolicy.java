package com.cache.strategy.distribution;

import java.util.List;

import com.cache.model.Key;
import com.cache.model.NodeKey;

public class ModuloDistributionPolicy implements DistributionPolicy {

    private volatile List<NodeKey> nodeList = List.of();

    @Override
    public void refreshNodes(List<NodeKey> updatedNodes) {
        this.nodeList = List.copyOf(updatedNodes);
    }

    @Override
    public NodeKey getTargetNode(Key key) {
        List<NodeKey> currentSnapshot = nodeList;
        if (currentSnapshot.isEmpty()) {
            return null;
        }

        int bucketIndex = Math.abs(key.hashCode()) % currentSnapshot.size();
        return currentSnapshot.get(bucketIndex);
    }
}
