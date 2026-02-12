package org.lytharalab.csch.safety.rules;

import org.lytharalab.csch.core.layer.SafetyRule;
import org.lytharalab.csch.core.layer.SafetyViolation;
import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;

import java.util.UUID;

public class CliffAvoidanceRule implements SafetyRule {
    private final String id;
    private final String name = "CliffAvoidance";
    private final String description = "防止玩家走向悬崖或高处掉落";
    private boolean enabled = true;
    
    private double minGroundDistance = 3.0;
    private double slowdownDistance = 5.0;
    
    public CliffAvoidanceRule() {
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
        
        if (action.getMoveForward() > 0 && !player.isOnGround()) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public MotorAction correct(MotorAction action, WorldState state) {
        if (!enabled || state == null) {
            return action;
        }
        
        return MotorAction.builder()
            .moveForward(0)
            .strafe(action.getStrafe())
            .yawRate(action.getYawRate())
            .pitchRate(action.getPitchRate())
            .jump(false)
            .sneak(true)
            .sprint(false)
            .attack(action.isAttack())
            .useItem(action.isUseItem())
            .build();
    }
    
    @Override
    public String getSuggestedAction(MotorAction action, WorldState state) {
        return "停止前进并蹲下以防止掉落";
    }
    
    public void setMinGroundDistance(double distance) {
        this.minGroundDistance = distance;
    }
    
    public void setSlowdownDistance(double distance) {
        this.slowdownDistance = distance;
    }
}
