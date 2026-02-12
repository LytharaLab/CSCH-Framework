package org.lytharalab.csch.core.layer;

import org.lytharalab.csch.core.intent.IntentGraph;
import org.lytharalab.csch.core.state.WorldState;

import java.util.concurrent.CompletableFuture;

public interface ConsciousLayer extends CSCHComponent {
    
    IntentGraph generateIntentGraph(String goal, WorldState currentState);
    
    IntentGraph updateIntentGraph(IntentGraph currentGraph, WorldState currentState);
    
    boolean shouldIntervene(WorldState currentState, IntentGraph currentGraph);
    
    String generateReflection(WorldState currentState, IntentGraph executedGraph);
    
    default CompletableFuture<IntentGraph> generateIntentGraphAsync(String goal, WorldState currentState) {
        return CompletableFuture.supplyAsync(() -> generateIntentGraph(goal, currentState));
    }
    
    default CompletableFuture<IntentGraph> updateIntentGraphAsync(IntentGraph currentGraph, WorldState currentState) {
        return CompletableFuture.supplyAsync(() -> updateIntentGraph(currentGraph, currentState));
    }
}
