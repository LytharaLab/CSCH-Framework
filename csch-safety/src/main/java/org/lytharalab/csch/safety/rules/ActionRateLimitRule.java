package org.lytharalab.csch.safety.rules;

import org.lytharalab.csch.core.layer.SafetyRule;
import org.lytharalab.csch.core.layer.SafetyViolation;
import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;

import java.util.UUID;

public class ActionRateLimitRule implements SafetyRule {
    private final String id;
    private final String name = "ActionRateLimit";
    private final String description = "限制动作变化速率以防止抖动";
    private boolean enabled = true;
    
    private double maxYawRate = Math.PI;
    private double maxPitchRate = Math.PI / 2;
    private double maxMoveChange = 0.5;
    
    private double lastYawRate = 0;
    private double lastPitchRate = 0;
    private double lastMoveForward = 0;
    private long lastUpdateTime = 0;
    
    public ActionRateLimitRule() {
        this.id = UUID.randomUUID().toString();
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public SafetyViolation.ViolationSeverity getSeverity() {
        return SafetyViolation.ViolationSeverity.WARNING;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public boolean violates(MotorAction action, WorldState state) {
        if (!enabled) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        double dt = (currentTime - lastUpdateTime) / 1000.0;
        
        if (dt < 0.001) {
            return false;
        }
        
        double yawRateChange = Math.abs(action.getYawRate() - lastYawRate) / dt;
        double pitchRateChange = Math.abs(action.getPitchRate() - lastPitchRate) / dt;
        double moveChange = Math.abs(action.getMoveForward() - lastMoveForward) / dt;
        
        return yawRateChange > maxYawRate || 
               pitchRateChange > maxPitchRate ||
               moveChange > maxMoveChange;
    }
    
    @Override
    public MotorAction correct(MotorAction action, WorldState state) {
        if (!enabled) {
            return action;
        }
        
        long currentTime = System.currentTimeMillis();
        double dt = (currentTime - lastUpdateTime) / 1000.0;
        
        if (dt < 0.001) {
            return action;
        }
        
        double correctedYawRate = action.getYawRate();
        double correctedPitchRate = action.getPitchRate();
        double correctedMoveForward = action.getMoveForward();
        
        double yawRateChange = action.getYawRate() - lastYawRate;
        if (Math.abs(yawRateChange) / dt > maxYawRate) {
            correctedYawRate = lastYawRate + Math.signum(yawRateChange) * maxYawRate * dt;
        }
        
        double pitchRateChange = action.getPitchRate() - lastPitchRate;
        if (Math.abs(pitchRateChange) / dt > maxPitchRate) {
            correctedPitchRate = lastPitchRate + Math.signum(pitchRateChange) * maxPitchRate * dt;
        }
        
        double moveChange = action.getMoveForward() - lastMoveForward;
        if (Math.abs(moveChange) / dt > maxMoveChange) {
            correctedMoveForward = lastMoveForward + Math.signum(moveChange) * maxMoveChange * dt;
        }
        
        lastYawRate = correctedYawRate;
        lastPitchRate = correctedPitchRate;
        lastMoveForward = correctedMoveForward;
        lastUpdateTime = currentTime;
        
        return MotorAction.builder()
            .moveForward(correctedMoveForward)
            .strafe(action.getStrafe())
            .yawRate(correctedYawRate)
            .pitchRate(correctedPitchRate)
            .jump(action.isJump())
            .sneak(action.isSneak())
            .sprint(action.isSprint())
            .attack(action.isAttack())
            .useItem(action.isUseItem())
            .build();
    }
    
    @Override
    public String getSuggestedAction(MotorAction action, WorldState state) {
        return "平滑动作变化以防止抖动";
    }
    
    public void setMaxYawRate(double rate) {
        this.maxYawRate = rate;
    }
    
    public void setMaxPitchRate(double rate) {
        this.maxPitchRate = rate;
    }
    
    public void setMaxMoveChange(double change) {
        this.maxMoveChange = change;
    }
    
    public void reset() {
        lastYawRate = 0;
        lastPitchRate = 0;
        lastMoveForward = 0;
        lastUpdateTime = System.currentTimeMillis();
    }
}
