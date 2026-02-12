package org.lytharalab.csch.conscious;

import org.lytharalab.csch.core.intent.IntentGraph;
import org.lytharalab.csch.core.intent.IntentNode;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;

import java.util.List;

public class InterventionDecider {
    
    private double healthThreshold = 0.3;
    private double stuckTimeThresholdMs = 5000;
    private double positionVarianceThreshold = 0.1;
    private int consecutiveFailureThreshold = 3;
    
    private int consecutiveFailures = 0;
    private long lastProgressTime = System.currentTimeMillis();
    private double lastX, lastY, lastZ;
    
    public boolean shouldIntervene(WorldState currentState, IntentGraph currentGraph) {
        if (currentState == null) {
            return false;
        }
        
        PlayerState player = currentState.getPlayerState();
        if (player == null) {
            return false;
        }
        
        if (checkHealthEmergency(player)) {
            return true;
        }
        
        if (checkStuckState(player)) {
            return true;
        }
        
        if (checkGraphFailure(currentGraph)) {
            return true;
        }
        
        updateProgressTracking(player);
        return false;
    }
    
    private boolean checkHealthEmergency(PlayerState player) {
        return player.getHealthRatio() < healthThreshold;
    }
    
    private boolean checkStuckState(PlayerState player) {
        long currentTime = System.currentTimeMillis();
        long timeSinceProgress = currentTime - lastProgressTime;
        
        if (timeSinceProgress > stuckTimeThresholdMs) {
            double positionVariance = Math.abs(player.getPositionX() - lastX) +
                Math.abs(player.getPositionY() - lastY) +
                Math.abs(player.getPositionZ() - lastZ);
            
            if (positionVariance < positionVarianceThreshold) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean checkGraphFailure(IntentGraph graph) {
        if (graph == null) {
            return false;
        }
        
        for (IntentNode node : graph.getNodes()) {
            if (node.isFailed()) {
                consecutiveFailures++;
                if (consecutiveFailures >= consecutiveFailureThreshold) {
                    return true;
                }
            } else if (node.isActive()) {
                consecutiveFailures = 0;
            }
        }
        
        return false;
    }
    
    private void updateProgressTracking(PlayerState player) {
        double positionVariance = Math.abs(player.getPositionX() - lastX) +
            Math.abs(player.getPositionY() - lastY) +
            Math.abs(player.getPositionZ() - lastZ);
        
        if (positionVariance > positionVarianceThreshold) {
            lastProgressTime = System.currentTimeMillis();
            lastX = player.getPositionX();
            lastY = player.getPositionY();
            lastZ = player.getPositionZ();
        }
    }
    
    public void reset() {
        consecutiveFailures = 0;
        lastProgressTime = System.currentTimeMillis();
    }
    
    public void setHealthThreshold(double threshold) {
        this.healthThreshold = threshold;
    }
    
    public void setStuckTimeThresholdMs(long thresholdMs) {
        this.stuckTimeThresholdMs = thresholdMs;
    }
    
    public void setConsecutiveFailureThreshold(int threshold) {
        this.consecutiveFailureThreshold = threshold;
    }
}
