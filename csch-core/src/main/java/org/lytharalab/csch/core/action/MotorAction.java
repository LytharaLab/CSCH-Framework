package org.lytharalab.csch.core.action;

import java.time.Instant;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class MotorAction {
    private final double moveForward;
    private final double strafe;
    private final double yawRate;
    private final double pitchRate;
    private final boolean jump;
    private final boolean sneak;
    private final boolean sprint;
    private final boolean attack;
    private final boolean useItem;
    private final Instant timestamp;
    private final Map<String, Object> metadata;
    
    private MotorAction(Builder builder) {
        this.moveForward = clamp(builder.moveForward, -1, 1);
        this.strafe = clamp(builder.strafe, -1, 1);
        this.yawRate = builder.yawRate;
        this.pitchRate = builder.pitchRate;
        this.jump = builder.jump;
        this.sneak = builder.sneak;
        this.sprint = builder.sprint;
        this.attack = builder.attack;
        this.useItem = builder.useItem;
        this.timestamp = Instant.now();
        this.metadata = Collections.unmodifiableMap(new HashMap<>(builder.metadata));
    }
    
    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    
    public double getMoveForward() { return moveForward; }
    public double getStrafe() { return strafe; }
    public double getYawRate() { return yawRate; }
    public double getPitchRate() { return pitchRate; }
    public boolean isJump() { return jump; }
    public boolean isSneak() { return sneak; }
    public boolean isSprint() { return sprint; }
    public boolean isAttack() { return attack; }
    public boolean isUseItem() { return useItem; }
    public Instant getTimestamp() { return timestamp; }
    public Map<String, Object> getMetadata() { return metadata; }
    
    public boolean isMoving() {
        return Math.abs(moveForward) > 0.01 || Math.abs(strafe) > 0.01;
    }
    
    public boolean isLooking() {
        return Math.abs(yawRate) > 0.01 || Math.abs(pitchRate) > 0.01;
    }
    
    public double getMovementMagnitude() {
        return Math.sqrt(moveForward * moveForward + strafe * strafe);
    }
    
    public double getLookMagnitude() {
        return Math.sqrt(yawRate * yawRate + pitchRate * pitchRate);
    }
    
    public MotorAction merge(MotorAction other) {
        return builder()
            .moveForward(other.moveForward != 0 ? other.moveForward : this.moveForward)
            .strafe(other.strafe != 0 ? other.strafe : this.strafe)
            .yawRate(other.yawRate != 0 ? other.yawRate : this.yawRate)
            .pitchRate(other.pitchRate != 0 ? other.pitchRate : this.pitchRate)
            .jump(other.jump || this.jump)
            .sneak(other.sneak || this.sneak)
            .sprint(other.sprint || this.sprint)
            .attack(other.attack || this.attack)
            .useItem(other.useItem || this.useItem)
            .build();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static MotorAction idle() {
        return builder().build();
    }
    
    public static class Builder {
        private double moveForward;
        private double strafe;
        private double yawRate;
        private double pitchRate;
        private boolean jump;
        private boolean sneak;
        private boolean sprint;
        private boolean attack;
        private boolean useItem;
        private final Map<String, Object> metadata = new HashMap<>();
        
        public Builder moveForward(double value) {
            this.moveForward = value;
            return this;
        }
        
        public Builder strafe(double value) {
            this.strafe = value;
            return this;
        }
        
        public Builder yawRate(double value) {
            this.yawRate = value;
            return this;
        }
        
        public Builder pitchRate(double value) {
            this.pitchRate = value;
            return this;
        }
        
        public Builder jump(boolean jump) {
            this.jump = jump;
            return this;
        }
        
        public Builder sneak(boolean sneak) {
            this.sneak = sneak;
            return this;
        }
        
        public Builder sprint(boolean sprint) {
            this.sprint = sprint;
            return this;
        }
        
        public Builder attack(boolean attack) {
            this.attack = attack;
            return this;
        }
        
        public Builder useItem(boolean useItem) {
            this.useItem = useItem;
            return this;
        }
        
        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }
        
        public MotorAction build() {
            return new MotorAction(this);
        }
    }
}
