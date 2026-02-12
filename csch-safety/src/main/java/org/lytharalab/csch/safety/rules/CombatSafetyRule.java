package org.lytharalab.csch.safety.rules;

import org.lytharalab.csch.core.layer.SafetyRule;
import org.lytharalab.csch.core.layer.SafetyViolation;
import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;
import org.lytharalab.csch.core.state.EntityInfo;

import java.util.UUID;
import java.util.List;

public class CombatSafetyRule implements SafetyRule {
    private final String id;
    private final String name = "CombatSafety";
    private final String description = "战斗安全规则，防止过度冒险";
    private boolean enabled = true;
    
    private double minHealthForCombat = 0.5;
    private double maxEnemyDistance = 20.0;
    private int maxNearbyEnemies = 3;
    
    public CombatSafetyRule() {
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
        return SafetyViolation.ViolationSeverity.ERROR;
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
        
        if (action.isAttack() && player.getHealthRatio() < minHealthForCombat) {
            return true;
        }
        
        List<EntityInfo> nearbyEntities = state.getNearbyEntities();
        int hostileCount = 0;
        
        for (EntityInfo entity : nearbyEntities) {
            if (entity.isHostile()) {
                double distance = entity.distanceTo(
                    player.getPositionX(),
                    player.getPositionY(),
                    player.getPositionZ()
                );
                
                if (distance < maxEnemyDistance) {
                    hostileCount++;
                }
            }
        }
        
        if (hostileCount > maxNearbyEnemies && action.isAttack()) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public MotorAction correct(MotorAction action, WorldState state) {
        if (!enabled || state == null) {
            return action;
        }
        
        PlayerState player = state.getPlayerState();
        
        if (player.getHealthRatio() < minHealthForCombat) {
            return MotorAction.builder()
                .moveForward(-0.5)
                .yawRate(action.getYawRate())
                .sprint(true)
                .build();
        }
        
        return MotorAction.builder()
            .moveForward(action.getMoveForward())
            .strafe(action.getStrafe())
            .yawRate(action.getYawRate())
            .pitchRate(action.getPitchRate())
            .jump(action.isJump())
            .sneak(action.isSneak())
            .sprint(action.isSprint())
            .attack(false)
            .useItem(action.isUseItem())
            .build();
    }
    
    @Override
    public String getSuggestedAction(MotorAction action, WorldState state) {
        if (state != null && state.getPlayerState() != null) {
            if (state.getPlayerState().getHealthRatio() < minHealthForCombat) {
                return "生命值过低，撤退并恢复";
            }
        }
        return "敌人过多，避免直接战斗";
    }
    
    public void setMinHealthForCombat(double threshold) {
        this.minHealthForCombat = threshold;
    }
    
    public void setMaxEnemyDistance(double distance) {
        this.maxEnemyDistance = distance;
    }
    
    public void setMaxNearbyEnemies(int count) {
        this.maxNearbyEnemies = count;
    }
}
