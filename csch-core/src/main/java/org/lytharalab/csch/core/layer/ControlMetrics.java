package org.lytharalab.csch.core.layer;

import java.time.Duration;

public class ControlMetrics {
    private final double aimError;
    private final double pathDeviation;
    private final double jerk;
    private final int collisionCount;
    private final Duration stuckTime;
    private final Duration timeToConverge;
    private final double smoothness;
    private final double stability;
    
    private ControlMetrics(Builder builder) {
        this.aimError = builder.aimError;
        this.pathDeviation = builder.pathDeviation;
        this.jerk = builder.jerk;
        this.collisionCount = builder.collisionCount;
        this.stuckTime = builder.stuckTime;
        this.timeToConverge = builder.timeToConverge;
        this.smoothness = builder.smoothness;
        this.stability = builder.stability;
    }
    
    public double getAimError() { return aimError; }
    public double getPathDeviation() { return pathDeviation; }
    public double getJerk() { return jerk; }
    public int getCollisionCount() { return collisionCount; }
    public Duration getStuckTime() { return stuckTime; }
    public Duration getTimeToConverge() { return timeToConverge; }
    public double getSmoothness() { return smoothness; }
    public double getStability() { return stability; }
    
    public double getOverallQuality() {
        double aimScore = 1.0 - Math.min(aimError, 1.0);
        double pathScore = 1.0 - Math.min(pathDeviation, 1.0);
        double jerkScore = 1.0 - Math.min(jerk / 10.0, 1.0);
        double collisionScore = Math.max(0, 1.0 - collisionCount * 0.1);
        
        return (aimScore + pathScore + jerkScore + collisionScore + smoothness + stability) / 6.0;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private double aimError;
        private double pathDeviation;
        private double jerk;
        private int collisionCount;
        private Duration stuckTime = Duration.ZERO;
        private Duration timeToConverge = Duration.ZERO;
        private double smoothness = 1.0;
        private double stability = 1.0;
        
        public Builder aimError(double aimError) {
            this.aimError = aimError;
            return this;
        }
        
        public Builder pathDeviation(double pathDeviation) {
            this.pathDeviation = pathDeviation;
            return this;
        }
        
        public Builder jerk(double jerk) {
            this.jerk = jerk;
            return this;
        }
        
        public Builder collisionCount(int count) {
            this.collisionCount = count;
            return this;
        }
        
        public Builder stuckTime(Duration duration) {
            this.stuckTime = duration;
            return this;
        }
        
        public Builder timeToConverge(Duration duration) {
            this.timeToConverge = duration;
            return this;
        }
        
        public Builder smoothness(double smoothness) {
            this.smoothness = smoothness;
            return this;
        }
        
        public Builder stability(double stability) {
            this.stability = stability;
            return this;
        }
        
        public ControlMetrics build() {
            return new ControlMetrics(this);
        }
    }
}
