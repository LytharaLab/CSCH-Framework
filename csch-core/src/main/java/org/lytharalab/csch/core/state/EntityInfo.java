package org.lytharalab.csch.core.state;

import java.util.Map;
import java.util.HashMap;

public class EntityInfo {
    private final String id;
    private final String type;
    private final double x, y, z;
    private final float yaw, pitch;
    private final double health;
    private final Map<String, Object> metadata;
    
    private EntityInfo(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.x = builder.x;
        this.y = builder.y;
        this.z = builder.z;
        this.yaw = builder.yaw;
        this.pitch = builder.pitch;
        this.health = builder.health;
        this.metadata = new HashMap<>(builder.metadata);
    }
    
    public String getId() { return id; }
    public String getType() { return type; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public double getHealth() { return health; }
    public Map<String, Object> getMetadata() { return metadata; }
    
    public double distanceTo(double px, double py, double pz) {
        double dx = x - px;
        double dy = y - py;
        double dz = z - pz;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public boolean isHostile() {
        return type != null && (type.contains("zombie") || type.contains("skeleton") 
            || type.contains("creeper") || type.contains("spider") || type.contains("enderman"));
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String id;
        private String type;
        private double x, y, z;
        private float yaw, pitch;
        private double health = -1;
        private final Map<String, Object> metadata = new HashMap<>();
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder type(String type) {
            this.type = type;
            return this;
        }
        
        public Builder position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }
        
        public Builder rotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
            return this;
        }
        
        public Builder health(double health) {
            this.health = health;
            return this;
        }
        
        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }
        
        public EntityInfo build() {
            return new EntityInfo(this);
        }
    }
}
