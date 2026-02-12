package org.lytharalab.csch.core.intent;

import org.lytharalab.csch.core.common.Priority;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class IntentGraph {
    private final String id;
    private final Intent rootIntent;
    private final List<IntentNode> nodes;
    private final List<IntentEdge> edges;
    
    private IntentGraph(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.rootIntent = builder.rootIntent;
        this.nodes = Collections.unmodifiableList(new ArrayList<>(builder.nodes));
        this.edges = Collections.unmodifiableList(new ArrayList<>(builder.edges));
    }
    
    public String getId() { return id; }
    public Intent getRootIntent() { return rootIntent; }
    public List<IntentNode> getNodes() { return nodes; }
    public List<IntentEdge> getEdges() { return edges; }
    
    public IntentNode getNode(String nodeId) {
        return nodes.stream()
            .filter(n -> n.getId().equals(nodeId))
            .findFirst()
            .orElse(null);
    }
    
    public List<IntentNode> getChildren(String parentId) {
        List<IntentNode> children = new ArrayList<>();
        for (IntentEdge edge : edges) {
            if (edge.getFromId().equals(parentId)) {
                IntentNode node = getNode(edge.getToId());
                if (node != null) {
                    children.add(node);
                }
            }
        }
        return children;
    }
    
    public List<IntentNode> getRootNodes() {
        List<IntentNode> roots = new ArrayList<>();
        for (IntentNode node : nodes) {
            boolean hasParent = edges.stream()
                .anyMatch(e -> e.getToId().equals(node.getId()));
            if (!hasParent) {
                roots.add(node);
            }
        }
        return roots;
    }
    
    public IntentNode findHighestPriorityNode() {
        return nodes.stream()
            .max((a, b) -> Integer.compare(
                a.getIntent().getPriority().getValue(),
                b.getIntent().getPriority().getValue()))
            .orElse(null);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String id;
        private Intent rootIntent;
        private final List<IntentNode> nodes = new ArrayList<>();
        private final List<IntentEdge> edges = new ArrayList<>();
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder rootIntent(Intent intent) {
            this.rootIntent = intent;
            return this;
        }
        
        public Builder addNode(IntentNode node) {
            this.nodes.add(node);
            return this;
        }
        
        public Builder addEdge(IntentEdge edge) {
            this.edges.add(edge);
            return this;
        }
        
        public Builder edge(String fromId, String toId) {
            this.edges.add(new IntentEdge(fromId, toId));
            return this;
        }
        
        public Builder nodes(List<IntentNode> nodeList) {
            this.nodes.addAll(nodeList);
            return this;
        }
        
        public Builder edges(List<IntentEdge> edgeList) {
            this.edges.addAll(edgeList);
            return this;
        }
        
        public IntentGraph build() {
            return new IntentGraph(this);
        }
    }
}
