package org.lytharalab.csch.core.state;

import java.time.Instant;

public class PlayerState {
    private final double positionX;
    private final double positionY;
    private final double positionZ;
    private final double velocityX;
    private final double velocityY;
    private final double velocityZ;
    private final float yaw;
    private final float pitch;
    private final double health;
    private final double maxHealth;
    private final double hunger;
    private final double maxHunger;
    private final boolean isOnGround;
    private final boolean isInWater;
    private final boolean isSprinting;
    private final Instant timestamp;
    
    private PlayerState(Builder builder) {
        this.positionX = builder.positionX;
        this.positionY = builder.positionY;
        this.positionZ = builder.positionZ;
        this.velocityX = builder.velocityX;
        this.velocityY = builder.velocityY;
        this.velocityZ = builder.velocityZ;
        this.yaw = builder.yaw;
        this.pitch = builder.pitch;
        this.health = builder.health;
        this.maxHealth = builder.maxHealth;
        this.hunger = builder.hunger;
        this.maxHunger = builder.maxHunger;
        this.isOnGround = builder.isOnGround;
        this.isInWater = builder.isInWater;
        this.isSprinting = builder.isSprinting;
        this.timestamp = Instant.now();
    }
    
    public double getPositionX() { return positionX; }
    public double getPositionY() { return positionY; }
    public double getPositionZ() { return positionZ; }
    public double getVelocityX() { return velocityX; }
    public double getVelocityY() { return velocityY; }
    public double getVelocityZ() { return velocityZ; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public double getHealth() { return health; }
    public double getMaxHealth() { return maxHealth; }
    public double getHunger() { return hunger; }
    public double getMaxHunger() { return maxHunger; }
    public boolean isOnGround() { return isOnGround; }
    public boolean isInWater() { return isInWater; }
    public boolean isSprinting() { return isSprinting; }
    public Instant getTimestamp() { return timestamp; }
    
    public double getHealthRatio() {
        return maxHealth > 0 ? health / maxHealth : 0;
    }
    
    public double getHungerRatio() {
        return maxHunger > 0 ? hunger / maxHunger : 0;
    }
    
    public double getSpeed() {
        return Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);
    }
    
    public double distanceTo(double x, double y, double z) {
        double dx = positionX - x;
        double dy = positionY - y;
        double dz = positionZ - z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public double horizontalDistanceTo(double x, double z) {
        double dx = positionX - x;
        double dz = positionZ - z;
        return Math.sqrt(dx * dx + dz * dz);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private double positionX, positionY, positionZ;
        private double velocityX, velocityY, velocityZ;
        private float yaw, pitch;
        private double health = 20.0, maxHealth = 20.0;
        private double hunger = 20.0, maxHunger = 20.0;
        private boolean isOnGround = true;
        private boolean isInWater = false;
        private boolean isSprinting = false;
        
        public Builder position(double x, double y, double z) {
            this.positionX = x;
            this.positionY = y;
            this.positionZ = z;
            return this;
        }
        
        public Builder velocity(double vx, double vy, double vz) {
            this.velocityX = vx;
            this.velocityY = vy;
            this.velocityZ = vz;
            return this;
        }
        
        public Builder rotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
            return this;
        }
        
        public Builder health(double health, double maxHealth) {
            this.health = health;
            this.maxHealth = maxHealth;
            return this;
        }
        
        public Builder hunger(double hunger, double maxHunger) {
            this.hunger = hunger;
            this.maxHunger = maxHunger;
            return this;
        }
        
        public Builder onGround(boolean onGround) {
            this.isOnGround = onGround;
            return this;
        }
        
        public Builder inWater(boolean inWater) {
            this.isInWater = inWater;
            return this;
        }
        
        public Builder sprinting(boolean sprinting) {
            this.isSprinting = sprinting;
            return this;
        }
        
        public PlayerState build() {
            return new PlayerState(this);
        }
    }
}
