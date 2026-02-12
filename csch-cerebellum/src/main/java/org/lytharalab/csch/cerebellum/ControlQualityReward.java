package org.lytharalab.csch.cerebellum;

import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;

import java.time.Duration;

public class ControlQualityReward {
    
    private double aimErrorWeight = 1.0;
    private double pathDeviationWeight = 0.5;
    private double jerkWeight = 0.3;
    private double collisionWeight = 2.0;
    private double stuckTimeWeight = 1.0;
    private double convergenceTimeWeight = 0.5;
    
    private double previousYaw = 0;
    private double previousPitch = 0;
    private double previousX = 0;
    private double previousZ = 0;
    private long stuckStartTime = 0;
    private boolean wasStuck = false;
    
    public double computeReward(WorldState previousState, WorldState currentState,
                               double aimError, boolean collision, boolean isStuck) {
        double reward = 0;
        
        reward -= aimErrorWeight * aimError;
        
        if (previousState != null && currentState != null) {
            PlayerState prevPlayer = previousState.getPlayerState();
            PlayerState currPlayer = currentState.getPlayerState();
            
            if (prevPlayer != null && currPlayer != null) {
                double pathDeviation = computePathDeviation(prevPlayer, currPlayer);
                reward -= pathDeviationWeight * pathDeviation;
                
                double jerk = computeJerk(prevPlayer, currPlayer);
                reward -= jerkWeight * jerk;
            }
        }
        
        if (collision) {
            reward -= collisionWeight;
        }
        
        if (isStuck) {
            if (!wasStuck) {
                stuckStartTime = System.currentTimeMillis();
                wasStuck = true;
            }
            long stuckDuration = System.currentTimeMillis() - stuckStartTime;
            reward -= stuckTimeWeight * (stuckDuration / 1000.0);
        } else {
            wasStuck = false;
        }
        
        return reward;
    }
    
    private double computePathDeviation(PlayerState prev, PlayerState curr) {
        if (prev == null || curr == null) return 0;
        
        double expectedX = previousX + (curr.getPositionX() - previousX);
        double expectedZ = previousZ + (curr.getPositionZ() - previousZ);
        
        double deviation = Math.sqrt(
            Math.pow(curr.getPositionX() - expectedX, 2) +
            Math.pow(curr.getPositionZ() - expectedZ, 2)
        );
        
        previousX = curr.getPositionX();
        previousZ = curr.getPositionZ();
        
        return deviation;
    }
    
    private double computeJerk(PlayerState prev, PlayerState curr) {
        if (prev == null || curr == null) return 0;
        
        double yawDiff = Math.abs(curr.getYaw() - previousYaw);
        double pitchDiff = Math.abs(curr.getPitch() - previousPitch);
        
        yawDiff = Math.min(yawDiff, 360 - yawDiff);
        
        previousYaw = curr.getYaw();
        previousPitch = curr.getPitch();
        
        return (yawDiff + pitchDiff) / 180.0;
    }
    
    public void reset() {
        previousYaw = 0;
        previousPitch = 0;
        previousX = 0;
        previousZ = 0;
        stuckStartTime = 0;
        wasStuck = false;
    }
    
    public void setAimErrorWeight(double weight) {
        this.aimErrorWeight = weight;
    }
    
    public void setPathDeviationWeight(double weight) {
        this.pathDeviationWeight = weight;
    }
    
    public void setJerkWeight(double weight) {
        this.jerkWeight = weight;
    }
    
    public void setCollisionWeight(double weight) {
        this.collisionWeight = weight;
    }
    
    public void setStuckTimeWeight(double weight) {
        this.stuckTimeWeight = weight;
    }
}
