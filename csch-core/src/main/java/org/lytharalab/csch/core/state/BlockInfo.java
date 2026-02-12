package org.lytharalab.csch.core.state;

public class BlockInfo {
    private final int x, y, z;
    private final String type;
    private final boolean isSolid;
    private final boolean isPassable;
    private final double hardness;
    
    private BlockInfo(Builder builder) {
        this.x = builder.x;
        this.y = builder.y;
        this.z = builder.z;
        this.type = builder.type;
        this.isSolid = builder.isSolid;
        this.isPassable = builder.isPassable;
        this.hardness = builder.hardness;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public String getType() { return type; }
    public boolean isSolid() { return isSolid; }
    public boolean isPassable() { return isPassable; }
    public double getHardness() { return hardness; }
    
    public double distanceTo(double px, double py, double pz) {
        double dx = x - px;
        double dy = y - py;
        double dz = z - pz;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public boolean isDangerous() {
        if (type == null) return false;
        return type.contains("lava") || type.contains("magma") || type.contains("fire")
            || type.contains("cactus") || type.contains("sweet_berry");
    }
    
    public boolean isBreakable() {
        return hardness >= 0 && !"air".equals(type) && !"bedrock".equals(type);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private int x, y, z;
        private String type = "air";
        private boolean isSolid = false;
        private boolean isPassable = true;
        private double hardness = 0;
        
        public Builder position(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }
        
        public Builder type(String type) {
            this.type = type;
            return this;
        }
        
        public Builder solid(boolean solid) {
            this.isSolid = solid;
            return this;
        }
        
        public Builder passable(boolean passable) {
            this.isPassable = passable;
            return this;
        }
        
        public Builder hardness(double hardness) {
            this.hardness = hardness;
            return this;
        }
        
        public BlockInfo build() {
            return new BlockInfo(this);
        }
    }
}
