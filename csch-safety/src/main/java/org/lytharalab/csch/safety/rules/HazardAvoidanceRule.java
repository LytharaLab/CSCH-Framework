package org.lytharalab.csch.safety.rules;

import org.lytharalab.csch.core.layer.SafetyRule;
import org.lytharalab.csch.core.layer.SafetyViolation;
import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;
import org.lytharalab.csch.core.state.BlockInfo;

import java.util.UUID;
import java.util.List;

public class HazardAvoidanceRule implements SafetyRule {
    private final String id;
    private final String name = "HazardAvoidance";
    private final String description = "防止玩家进入危险区域（岩浆、仙人掌等）";
    private boolean enabled = true;
    
    private double hazardDetectionRange = 3.0;
    
    public HazardAvoidanceRule() {
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
        return SafetyViolation.ViolationSeverity.EMERGENCY;
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
        
        List<BlockInfo> nearbyBlocks = state.getNearbyBlocks();
        PlayerState player = state.getPlayerState();
        
        for (BlockInfo block : nearbyBlocks) {
            if (block.isDangerous()) {
                double distance = block.distanceTo(
                    player.getPositionX(),
                    player.getPositionY(),
                    player.getPositionZ()
                );
                
                if (distance < hazardDetectionRange && action.isMoving()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public MotorAction correct(MotorAction action, WorldState state) {
        if (!enabled || state == null || state.getPlayerState() == null) {
            return action;
        }
        
        PlayerState player = state.getPlayerState();
        List<BlockInfo> nearbyBlocks = state.getNearbyBlocks();
        
        double escapeX = 0;
        double escapeZ = 0;
        
        for (BlockInfo block : nearbyBlocks) {
            if (block.isDangerous()) {
                double dx = player.getPositionX() - block.getX();
                double dz = player.getPositionZ() - block.getZ();
                double dist = Math.sqrt(dx * dx + dz * dz);
                
                if (dist > 0.01) {
                    escapeX += dx / dist;
                    escapeZ += dz / dist;
                }
            }
        }
        
        double escapeMag = Math.sqrt(escapeX * escapeX + escapeZ * escapeZ);
        if (escapeMag > 0.01) {
            escapeX /= escapeMag;
            escapeZ /= escapeMag;
        }
        
        return MotorAction.builder()
            .moveForward(1.0)
            .strafe(escapeZ)
            .yawRate(escapeX * Math.PI / 4)
            .sprint(true)
            .build();
    }
    
    @Override
    public String getSuggestedAction(MotorAction action, WorldState state) {
        return "立即远离危险区域";
    }
    
    public void setHazardDetectionRange(double range) {
        this.hazardDetectionRange = range;
    }
}
