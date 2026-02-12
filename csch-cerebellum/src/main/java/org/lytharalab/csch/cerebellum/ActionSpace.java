package org.lytharalab.csch.cerebellum;

import java.util.Map;
import java.util.HashMap;

public class ActionSpace {
    private final double moveForwardMin;
    private final double moveForwardMax;
    private final double strafeMin;
    private final double strafeMax;
    private final double yawRateMin;
    private final double yawRateMax;
    private final double pitchRateMin;
    private final double pitchRateMax;
    private final boolean jumpAllowed;
    private final boolean sneakAllowed;
    private final boolean sprintAllowed;
    
    private ActionSpace(Builder builder) {
        this.moveForwardMin = builder.moveForwardMin;
        this.moveForwardMax = builder.moveForwardMax;
        this.strafeMin = builder.strafeMin;
        this.strafeMax = builder.strafeMax;
        this.yawRateMin = builder.yawRateMin;
        this.yawRateMax = builder.yawRateMax;
        this.pitchRateMin = builder.pitchRateMin;
        this.pitchRateMax = builder.pitchRateMax;
        this.jumpAllowed = builder.jumpAllowed;
        this.sneakAllowed = builder.sneakAllowed;
        this.sprintAllowed = builder.sprintAllowed;
    }
    
    public double getMoveForwardMin() { return moveForwardMin; }
    public double getMoveForwardMax() { return moveForwardMax; }
    public double getStrafeMin() { return strafeMin; }
    public double getStrafeMax() { return strafeMax; }
    public double getYawRateMin() { return yawRateMin; }
    public double getYawRateMax() { return yawRateMax; }
    public double getPitchRateMin() { return pitchRateMin; }
    public double getPitchRateMax() { return pitchRateMax; }
    public boolean isJumpAllowed() { return jumpAllowed; }
    public boolean isSneakAllowed() { return sneakAllowed; }
    public boolean isSprintAllowed() { return sprintAllowed; }
    
    public double clampMoveForward(double value) {
        return Math.max(moveForwardMin, Math.min(moveForwardMax, value));
    }
    
    public double clampStrafe(double value) {
        return Math.max(strafeMin, Math.min(strafeMax, value));
    }
    
    public double clampYawRate(double value) {
        return Math.max(yawRateMin, Math.min(yawRateMax, value));
    }
    
    public double clampPitchRate(double value) {
        return Math.max(pitchRateMin, Math.min(pitchRateMax, value));
    }
    
    public int getContinuousActionDimension() {
        return 4;
    }
    
    public int getDiscreteActionDimension() {
        int count = 0;
        if (jumpAllowed) count++;
        if (sneakAllowed) count++;
        if (sprintAllowed) count++;
        return count;
    }
    
    public static ActionSpace createDefault() {
        return builder()
            .moveForwardRange(0, 1)
            .strafeRange(-1, 1)
            .yawRateRange(-Math.PI / 2, Math.PI / 2)
            .pitchRateRange(-Math.PI / 4, Math.PI / 4)
            .jumpAllowed(true)
            .sneakAllowed(true)
            .sprintAllowed(true)
            .build();
    }
    
    public static ActionSpace createRestricted() {
        return builder()
            .moveForwardRange(0, 0.8)
            .strafeRange(-0.5, 0.5)
            .yawRateRange(-Math.PI / 4, Math.PI / 4)
            .pitchRateRange(-Math.PI / 8, Math.PI / 8)
            .jumpAllowed(false)
            .sneakAllowed(true)
            .sprintAllowed(false)
            .build();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private double moveForwardMin = 0;
        private double moveForwardMax = 1;
        private double strafeMin = -1;
        private double strafeMax = 1;
        private double yawRateMin = -Math.PI / 2;
        private double yawRateMax = Math.PI / 2;
        private double pitchRateMin = -Math.PI / 4;
        private double pitchRateMax = Math.PI / 4;
        private boolean jumpAllowed = true;
        private boolean sneakAllowed = true;
        private boolean sprintAllowed = true;
        
        public Builder moveForwardRange(double min, double max) {
            this.moveForwardMin = min;
            this.moveForwardMax = max;
            return this;
        }
        
        public Builder strafeRange(double min, double max) {
            this.strafeMin = min;
            this.strafeMax = max;
            return this;
        }
        
        public Builder yawRateRange(double min, double max) {
            this.yawRateMin = min;
            this.yawRateMax = max;
            return this;
        }
        
        public Builder pitchRateRange(double min, double max) {
            this.pitchRateMin = min;
            this.pitchRateMax = max;
            return this;
        }
        
        public Builder jumpAllowed(boolean allowed) {
            this.jumpAllowed = allowed;
            return this;
        }
        
        public Builder sneakAllowed(boolean allowed) {
            this.sneakAllowed = allowed;
            return this;
        }
        
        public Builder sprintAllowed(boolean allowed) {
            this.sprintAllowed = allowed;
            return this;
        }
        
        public ActionSpace build() {
            return new ActionSpace(this);
        }
    }
}
