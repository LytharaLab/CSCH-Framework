package org.lytharalab.csch.conscious;

import org.lytharalab.csch.core.intent.*;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;
import org.lytharalab.csch.core.common.Priority;
import org.lytharalab.csch.core.layer.CSCHException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class SimpleConsciousLayer extends AbstractConsciousLayer {
    
    private final IntentDecomposer intentDecomposer;
    private final InterventionDecider interventionDecider;
    private final ReflectionGenerator reflectionGenerator;
    private IntentGraph currentGraph;
    
    public SimpleConsciousLayer() {
        this.intentDecomposer = new IntentDecomposer();
        this.interventionDecider = new InterventionDecider();
        this.reflectionGenerator = new ReflectionGenerator();
    }
    
    @Override
    protected void doInitialize() throws Exception {
        logger.info("Initializing SimpleConsciousLayer");
    }
    
    @Override
    protected void doShutdown() throws Exception {
        logger.info("Shutting down SimpleConsciousLayer");
        currentGraph = null;
    }
    
    @Override
    public IntentGraph generateIntentGraph(String goal, WorldState currentState) {
        logger.debug("Generating intent graph for goal: {}", goal);
        
        Intent rootIntent = parseGoal(goal, currentState);
        
        IntentGraphBuilder builder = IntentGraphBuilder.create(goal, currentState);
        
        List<Intent> subIntents = intentDecomposer.decompose(rootIntent, currentState);
        
        String rootId = "root";
        builder.addIntent(rootIntent, null);
        
        for (Intent subIntent : subIntents) {
            builder.addIntent(subIntent, rootId);
        }
        
        currentGraph = builder.build();
        return currentGraph;
    }
    
    @Override
    public IntentGraph updateIntentGraph(IntentGraph currentGraph, WorldState currentState) {
        if (currentGraph == null) {
            return currentGraph;
        }
        
        boolean needsUpdate = false;
        
        for (IntentNode node : currentGraph.getNodes()) {
            if (node.getStatus() == IntentNodeStatus.FAILED) {
                needsUpdate = true;
                break;
            }
        }
        
        if (needsUpdate) {
            logger.debug("Intent graph needs update due to failures");
            return generateIntentGraph(currentGraph.getRootIntent().getDescription(), currentState);
        }
        
        return currentGraph;
    }
    
    @Override
    public boolean shouldIntervene(WorldState currentState, IntentGraph currentGraph) {
        return interventionDecider.shouldIntervene(currentState, currentGraph);
    }
    
    @Override
    public String generateReflection(WorldState currentState, IntentGraph executedGraph) {
        return reflectionGenerator.generateReflection(currentState, executedGraph);
    }
    
    private Intent parseGoal(String goal, WorldState state) {
        IntentType type = inferIntentType(goal);
        Priority priority = inferPriority(goal, state);
        Map<String, Object> params = extractParameters(goal);
        
        return Intent.builder()
            .description(goal)
            .type(type)
            .priority(priority)
            .parameters(params)
            .build();
    }
    
    private IntentType inferIntentType(String goal) {
        String lowerGoal = goal.toLowerCase();
        
        if (lowerGoal.contains("去") || lowerGoal.contains("移动") || lowerGoal.contains("导航")) {
            return IntentType.NAVIGATE;
        }
        if (lowerGoal.contains("挖") || lowerGoal.contains("采集") || lowerGoal.contains("矿")) {
            return IntentType.MINE;
        }
        if (lowerGoal.contains("战斗") || lowerGoal.contains("攻击") || lowerGoal.contains("杀")) {
            return IntentType.COMBAT;
        }
        if (lowerGoal.contains("合成") || lowerGoal.contains("制作")) {
            return IntentType.CRAFT;
        }
        if (lowerGoal.contains("建造") || lowerGoal.contains("放置")) {
            return IntentType.BUILD;
        }
        if (lowerGoal.contains("生存") || lowerGoal.contains("恢复") || lowerGoal.contains("补血")) {
            return IntentType.SURVIVE;
        }
        if (lowerGoal.contains("探索") || lowerGoal.contains("寻找")) {
            return IntentType.EXPLORE;
        }
        
        return IntentType.GENERIC;
    }
    
    private Priority inferPriority(String goal, WorldState state) {
        String lowerGoal = goal.toLowerCase();
        
        if (lowerGoal.contains("紧急") || lowerGoal.contains("立即")) {
            return Priority.CRITICAL;
        }
        if (lowerGoal.contains("重要")) {
            return Priority.HIGH;
        }
        
        if (state != null && state.getPlayerState() != null) {
            PlayerState player = state.getPlayerState();
            if (player.getHealthRatio() < 0.3) {
                return Priority.CRITICAL;
            }
        }
        
        return Priority.NORMAL;
    }
    
    private Map<String, Object> extractParameters(String goal) {
        Map<String, Object> params = new HashMap<>();
        
        String[] words = goal.split("\\s+");
        for (String word : words) {
            if (word.matches("\\d+")) {
                params.put("amount", Integer.parseInt(word));
            }
        }
        
        return params;
    }
}
