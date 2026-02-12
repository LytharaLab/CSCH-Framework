package org.lytharalab.csch.safety.rules;

import org.lytharalab.csch.core.layer.SafetyRule;
import org.lytharalab.csch.core.layer.SafetyViolation;
import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;

import java.util.UUID;

public class HealthProtectionRule implements SafetyRule {
    private final String id;
    private final String name = "HealthProtection";
    private final String description = "当生命值过低时限制危险行为";
    private boolean enabled = true;
    
    private double criticalHealthThreshold = 0.2;
    private double lowHealthThreshold = 0.4;
    
    public HealthProtectionRule() {
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
        return SafetyViolation.ViolationSeverity.CRITICAL;
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
        if (!enabled || state == null || state.getPlayerState() == null) {
            return false;
        }
        
        PlayerState player = state.getPlayerState();
        double healthRatio = player.getHealthRatio();
        
        if (healthRatio < criticalHealthThreshold) {
            return action.isSprint() || action.isAttack();
        }
        
        if (healthRatio < lowHealthThreshold) {
            return action.isSprint();
        }
        
        return false;
    }
    
    @Override
    public MotorAction correct(MotorAction action, WorldState state) {
        if (!enabled || state == null) {
            return action;
        }
        
        return MotorAction.builder()
            .moveForward(action.getMoveForward() * 0.5)
            .strafe(action.getStrafe())
            .yawRate(action.getYawRate())
            .pitchRate(action.getPitchRate())
            .jump(false)
            .sneak(true)
            .sprint(false)
            .attack(false)
            .useItem(action.isUseItem())
            .build();
    }
    
    @Override
    public String getSuggestedAction(MotorAction action, WorldState state) {
        if (state != null && state.getPlayerState() != null) {
            double healthRatio = state.getPlayerState().getHealthRatio();
            if (healthRatio < criticalHealthThreshold) {
                return "生命值危急，停止所有危险行为并寻找安全地点";
            }
            return "生命值较低，降低活动强度";
        }
        return "保护生命值";
    }
    
    public void setCriticalHealthThreshold(double threshold) {
        this.criticalHealthThreshold = threshold;
    }
    
    public void setLowHealthThreshold(double threshold) {
        this.lowHealthThreshold = threshold;
    }
}
