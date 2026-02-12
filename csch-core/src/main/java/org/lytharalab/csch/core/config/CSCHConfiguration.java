package org.lytharalab.csch.core.config;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class CSCHConfiguration {
    private final int controlFrequencyHz;
    private final int intentUpdateIntervalMs;
    private final int skillUpdateIntervalMs;
    private final int motorUpdateIntervalMs;
    private final double actionSmoothnessThreshold;
    private final double collisionThreshold;
    private final double aimErrorThreshold;
    private final boolean safetyShieldEnabled;
    private final double safetyShieldThreshold;
    private final boolean openclEnabled;
    private final int maxActionHistorySize;
    private final int maxSkillHistorySize;
    private final int maxIntentHistorySize;
    private final Map<String, Object> customProperties;
    
    private CSCHConfiguration(Builder builder) {
        this.controlFrequencyHz = builder.controlFrequencyHz;
        this.intentUpdateIntervalMs = builder.intentUpdateIntervalMs;
        this.skillUpdateIntervalMs = builder.skillUpdateIntervalMs;
        this.motorUpdateIntervalMs = builder.motorUpdateIntervalMs;
        this.actionSmoothnessThreshold = builder.actionSmoothnessThreshold;
        this.collisionThreshold = builder.collisionThreshold;
        this.aimErrorThreshold = builder.aimErrorThreshold;
        this.safetyShieldEnabled = builder.safetyShieldEnabled;
        this.safetyShieldThreshold = builder.safetyShieldThreshold;
        this.openclEnabled = builder.openclEnabled;
        this.maxActionHistorySize = builder.maxActionHistorySize;
        this.maxSkillHistorySize = builder.maxSkillHistorySize;
        this.maxIntentHistorySize = builder.maxIntentHistorySize;
        this.customProperties = Collections.unmodifiableMap(new HashMap<>(builder.customProperties));
    }
    
    public int getControlFrequencyHz() { return controlFrequencyHz; }
    public int getIntentUpdateIntervalMs() { return intentUpdateIntervalMs; }
    public int getSkillUpdateIntervalMs() { return skillUpdateIntervalMs; }
    public int getMotorUpdateIntervalMs() { return motorUpdateIntervalMs; }
    public double getActionSmoothnessThreshold() { return actionSmoothnessThreshold; }
    public double getCollisionThreshold() { return collisionThreshold; }
    public double getAimErrorThreshold() { return aimErrorThreshold; }
    public boolean isSafetyShieldEnabled() { return safetyShieldEnabled; }
    public double getSafetyShieldThreshold() { return safetyShieldThreshold; }
    public boolean isOpenclEnabled() { return openclEnabled; }
    public int getMaxActionHistorySize() { return maxActionHistorySize; }
    public int getMaxSkillHistorySize() { return maxSkillHistorySize; }
    public int getMaxIntentHistorySize() { return maxIntentHistorySize; }
    public Map<String, Object> getCustomProperties() { return customProperties; }
    
    @SuppressWarnings("unchecked")
    public <T> T getCustomProperty(String key) {
        return (T) customProperties.get(key);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getCustomProperty(String key, T defaultValue) {
        Object value = customProperties.get(key);
        return value != null ? (T) value : defaultValue;
    }
    
    public static CSCHConfiguration defaultConfiguration() {
        return builder().build();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private int controlFrequencyHz = 60;
        private int intentUpdateIntervalMs = 1000;
        private int skillUpdateIntervalMs = 100;
        private int motorUpdateIntervalMs = 16;
        private double actionSmoothnessThreshold = 0.1;
        private double collisionThreshold = 0.5;
        private double aimErrorThreshold = 0.05;
        private boolean safetyShieldEnabled = true;
        private double safetyShieldThreshold = 0.8;
        private boolean openclEnabled = false;
        private int maxActionHistorySize = 1000;
        private int maxSkillHistorySize = 100;
        private int maxIntentHistorySize = 50;
        private final Map<String, Object> customProperties = new HashMap<>();
        
        public Builder controlFrequencyHz(int hz) {
            this.controlFrequencyHz = hz;
            return this;
        }
        
        public Builder intentUpdateIntervalMs(int ms) {
            this.intentUpdateIntervalMs = ms;
            return this;
        }
        
        public Builder skillUpdateIntervalMs(int ms) {
            this.skillUpdateIntervalMs = ms;
            return this;
        }
        
        public Builder motorUpdateIntervalMs(int ms) {
            this.motorUpdateIntervalMs = ms;
            return this;
        }
        
        public Builder actionSmoothnessThreshold(double threshold) {
            this.actionSmoothnessThreshold = threshold;
            return this;
        }
        
        public Builder collisionThreshold(double threshold) {
            this.collisionThreshold = threshold;
            return this;
        }
        
        public Builder aimErrorThreshold(double threshold) {
            this.aimErrorThreshold = threshold;
            return this;
        }
        
        public Builder safetyShieldEnabled(boolean enabled) {
            this.safetyShieldEnabled = enabled;
            return this;
        }
        
        public Builder safetyShieldThreshold(double threshold) {
            this.safetyShieldThreshold = threshold;
            return this;
        }
        
        public Builder openclEnabled(boolean enabled) {
            this.openclEnabled = enabled;
            return this;
        }
        
        public Builder maxActionHistorySize(int size) {
            this.maxActionHistorySize = size;
            return this;
        }
        
        public Builder maxSkillHistorySize(int size) {
            this.maxSkillHistorySize = size;
            return this;
        }
        
        public Builder maxIntentHistorySize(int size) {
            this.maxIntentHistorySize = size;
            return this;
        }
        
        public Builder customProperty(String key, Object value) {
            this.customProperties.put(key, value);
            return this;
        }
        
        public CSCHConfiguration build() {
            return new CSCHConfiguration(this);
        }
    }
}
