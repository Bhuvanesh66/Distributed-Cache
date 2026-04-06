package com.cache.strategy.distribution;

import java.util.List;

import com.cache.model.Key;
import com.cache.model.NodeKey;

public interface DistributionPolicy {
    void refreshNodes(List<NodeKey> activeNodes);
    NodeKey getTargetNode(Key key);
}
