package org.lytharalab.csch.core.state;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class EnvironmentState {
    private final Map<String, Object> properties;
    private final long worldTime;
    private final String dimension;
    private final String biome;
    private final double lightLevel;
    private final boolean isRaining;
    private final boolean isThundering;
    
    private EnvironmentState(Builder builder) {
        this.properties = Collections.unmodifiableMap(new HashMap<>(builder.properties));
        this.worldTime = builder.worldTime;
        this.dimension = builder.dimension;
        this.biome = builder.biome;
        this.lightLevel = builder.lightLevel;
        this.isRaining = builder.isRaining;
        this.isThundering = builder.isThundering;
    }
    
    public Map<String, Object> getProperties() { return properties; }
    public long getWorldTime() { return worldTime; }
    public String getDimension() { return dimension; }
    public String getBiome() { return biome; }
    public double getLightLevel() { return lightLevel; }
    public boolean isRaining() { return isRaining; }
    public boolean isThundering() { return isThundering; }
    
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key) {
        return (T) properties.get(key);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, T defaultValue) {
        Object value = properties.get(key);
        return value != null ? (T) value : defaultValue;
    }
    
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }
    
    public boolean isDaytime() {
        long dayTime = worldTime % 24000;
        return dayTime >= 0 && dayTime < 13000;
    }
    
    public boolean isNighttime() {
        return !isDaytime();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final Map<String, Object> properties = new HashMap<>();
        private long worldTime = 0;
        private String dimension = "overworld";
        private String biome = "plains";
        private double lightLevel = 15.0;
        private boolean isRaining = false;
        private boolean isThundering = false;
        
        public Builder property(String key, Object value) {
            this.properties.put(key, value);
            return this;
        }
        
        public Builder properties(Map<String, Object> props) {
            this.properties.putAll(props);
            return this;
        }
        
        public Builder worldTime(long worldTime) {
            this.worldTime = worldTime;
            return this;
        }
        
        public Builder dimension(String dimension) {
            this.dimension = dimension;
            return this;
        }
        
        public Builder biome(String biome) {
            this.biome = biome;
            return this;
        }
        
        public Builder lightLevel(double lightLevel) {
            this.lightLevel = lightLevel;
            return this;
        }
        
        public Builder raining(boolean raining) {
            this.isRaining = raining;
            return this;
        }
        
        public Builder thundering(boolean thundering) {
            this.isThundering = thundering;
            return this;
        }
        
        public EnvironmentState build() {
            return new EnvironmentState(this);
        }
    }
}
