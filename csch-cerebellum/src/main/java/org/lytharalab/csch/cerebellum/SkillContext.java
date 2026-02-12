package org.lytharalab.csch.cerebellum;

import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.skill.SkillCall;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;

import java.util.Map;
import java.util.HashMap;

public class SkillContext {
    private final SkillCall skillCall;
    private final WorldState worldState;
    private final Map<String, Object> contextData;
    private double targetYaw;
    private double targetPitch;
    private double targetX;
    private double targetY;
    private double targetZ;
    private double speed;
    private boolean cautious;
    
    public SkillContext(SkillCall skillCall, WorldState worldState) {
        this.skillCall = skillCall;
        this.worldState = worldState;
        this.contextData = new HashMap<>();
        this.speed = 1.0;
        this.cautious = false;
        
        initializeFromSkillCall();
    }
    
    private void initializeFromSkillCall() {
        if (skillCall == null) return;
        
        String skillName = skillCall.getSkillName();
        
        switch (skillName) {
            case "NavigateTo" -> initNavigationContext();
            case "AlignCrosshair" -> initAlignContext();
            case "Mine" -> initMineContext();
            case "CombatKite" -> initCombatContext();
            case "Escape" -> initEscapeContext();
            default -> initDefaultContext();
        }
    }
    
    private void initNavigationContext() {
        String target = skillCall.getParameter("target");
        speed = skillCall.getParameter("speed", 1.0);
        cautious = skillCall.getParameter("cautious", false);
        
        if (worldState != null && worldState.getPlayerState() != null) {
            PlayerState player = worldState.getPlayerState();
            targetX = player.getPositionX() + 10;
            targetY = player.getPositionY();
            targetZ = player.getPositionZ() + 10;
        }
        
        contextData.put("mode", "navigation");
    }
    
    private void initAlignContext() {
        String target = skillCall.getParameter("target");
        double tolerance = skillCall.getParameter("tolerance", 0.05);
        
        targetYaw = 0;
        targetPitch = 0;
        
        contextData.put("mode", "alignment");
        contextData.put("tolerance", tolerance);
    }
    
    private void initMineContext() {
        String resource = skillCall.getParameter("resource");
        int amount = skillCall.getParameter("amount", 1);
        
        contextData.put("mode", "mining");
        contextData.put("resource", resource);
        contextData.put("amount", amount);
    }
    
    private void initCombatContext() {
        String target = skillCall.getParameter("target");
        double distance = skillCall.getParameter("distance", 3.5);
        
        contextData.put("mode", "combat");
        contextData.put("target", target);
        contextData.put("distance", distance);
    }
    
    private void initEscapeContext() {
        String threat = skillCall.getParameter("threat");
        double minDistance = skillCall.getParameter("minDistance", 10.0);
        
        speed = 1.5;
        
        contextData.put("mode", "escape");
        contextData.put("threat", threat);
        contextData.put("minDistance", minDistance);
    }
    
    private void initDefaultContext() {
        contextData.put("mode", "default");
    }
    
    public SkillCall getSkillCall() { return skillCall; }
    public WorldState getWorldState() { return worldState; }
    public double getTargetYaw() { return targetYaw; }
    public double getTargetPitch() { return targetPitch; }
    public double getTargetX() { return targetX; }
    public double getTargetY() { return targetY; }
    public double getTargetZ() { return targetZ; }
    public double getSpeed() { return speed; }
    public boolean isCautious() { return cautious; }
    
    public void setTargetYaw(double yaw) { this.targetYaw = yaw; }
    public void setTargetPitch(double pitch) { this.targetPitch = pitch; }
    public void setTargetPosition(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getContextData(String key) {
        return (T) contextData.get(key);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getContextData(String key, T defaultValue) {
        Object value = contextData.get(key);
        return value != null ? (T) value : defaultValue;
    }
    
    public void setContextData(String key, Object value) {
        contextData.put(key, value);
    }
    
    public String getMode() {
        return getContextData("mode", "default");
    }
    
    public double computeYawError() {
        if (worldState == null || worldState.getPlayerState() == null) return 0;
        PlayerState player = worldState.getPlayerState();
        return normalizeAngle(targetYaw - player.getYaw());
    }
    
    public double computePitchError() {
        if (worldState == null || worldState.getPlayerState() == null) return 0;
        PlayerState player = worldState.getPlayerState();
        return normalizeAngle(targetPitch - player.getPitch());
    }
    
    public double computeDistanceToTarget() {
        if (worldState == null || worldState.getPlayerState() == null) return 0;
        PlayerState player = worldState.getPlayerState();
        return player.distanceTo(targetX, targetY, targetZ);
    }
    
    private double normalizeAngle(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
}
