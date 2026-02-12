package org.lytharalab.csch.conscious;

import org.lytharalab.csch.core.intent.*;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;
import org.lytharalab.csch.core.common.Priority;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class IntentGraphBuilder {
    
    private final String rootGoal;
    private final WorldState currentState;
    private final List<IntentNode> nodes = new ArrayList<>();
    private final List<IntentEdge> edges = new ArrayList<>();
    private int nodeCounter = 0;
    
    public IntentGraphBuilder(String rootGoal, WorldState currentState) {
        this.rootGoal = rootGoal;
        this.currentState = currentState;
    }
    
    public IntentGraphBuilder addIntent(Intent intent, String parentId) {
        String nodeId = "node_" + (++nodeCounter);
        IntentNode node = IntentNode.builder()
            .id(nodeId)
            .intent(intent)
            .build();
        nodes.add(node);
        
        if (parentId != null) {
            edges.add(new IntentEdge(parentId, nodeId));
        }
        
        return this;
    }
    
    public IntentGraphBuilder addSequentialIntents(List<Intent> intents, String parentId) {
        String prevId = parentId;
        for (Intent intent : intents) {
            String nodeId = "node_" + (++nodeCounter);
            IntentNode node = IntentNode.builder()
                .id(nodeId)
                .intent(intent)
                .build();
            nodes.add(node);
            
            if (prevId != null) {
                edges.add(new IntentEdge(prevId, nodeId));
            }
            prevId = nodeId;
        }
        return this;
    }
    
    public IntentGraphBuilder addParallelIntents(List<Intent> intents, String parentId) {
        for (Intent intent : intents) {
            String nodeId = "node_" + (++nodeCounter);
            IntentNode node = IntentNode.builder()
                .id(nodeId)
                .intent(intent)
                .build();
            nodes.add(node);
            
            if (parentId != null) {
                edges.add(new IntentEdge(parentId, nodeId, IntentEdge.EdgeType.PARALLEL, new HashMap<>()));
            }
        }
        return this;
    }
    
    public IntentGraph build() {
        Intent rootIntent = Intent.builder()
            .description(rootGoal)
            .type(IntentType.GENERIC)
            .priority(Priority.HIGH)
            .build();
        
        return IntentGraph.builder()
            .rootIntent(rootIntent)
            .nodes(nodes)
            .edges(edges)
            .build();
    }
    
    public static IntentGraphBuilder create(String rootGoal, WorldState currentState) {
        return new IntentGraphBuilder(rootGoal, currentState);
    }
}
