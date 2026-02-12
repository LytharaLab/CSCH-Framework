package org.lytharalab.csch.core.state;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class WorldState {
    private final PlayerState playerState;
    private final EnvironmentState environmentState;
    private final List<EntityInfo> nearbyEntities;
    private final List<BlockInfo> nearbyBlocks;
    private final Instant timestamp;
    
    private WorldState(Builder builder) {
        this.playerState = builder.playerState;
        this.environmentState = builder.environmentState;
        this.nearbyEntities = Collections.unmodifiableList(new ArrayList<>(builder.nearbyEntities));
        this.nearbyBlocks = Collections.unmodifiableList(new ArrayList<>(builder.nearbyBlocks));
        this.timestamp = Instant.now();
    }
    
    public PlayerState getPlayerState() { return playerState; }
    public EnvironmentState getEnvironmentState() { return environmentState; }
    public List<EntityInfo> getNearbyEntities() { return nearbyEntities; }
    public List<BlockInfo> getNearbyBlocks() { return nearbyBlocks; }
    public Instant getTimestamp() { return timestamp; }
    
    public List<EntityInfo> getEntitiesByType(String type) {
        List<EntityInfo> result = new ArrayList<>();
        for (EntityInfo entity : nearbyEntities) {
            if (entity.getType().equals(type)) {
                result.add(entity);
            }
        }
        return result;
    }
    
    public List<EntityInfo> getEntitiesInRange(double range) {
        if (playerState == null) return Collections.emptyList();
        List<EntityInfo> result = new ArrayList<>();
        for (EntityInfo entity : nearbyEntities) {
            double dist = playerState.distanceTo(entity.getX(), entity.getY(), entity.getZ());
            if (dist <= range) {
                result.add(entity);
            }
        }
        return result;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private PlayerState playerState;
        private EnvironmentState environmentState;
        private final List<EntityInfo> nearbyEntities = new ArrayList<>();
        private final List<BlockInfo> nearbyBlocks = new ArrayList<>();
        
        public Builder playerState(PlayerState playerState) {
            this.playerState = playerState;
            return this;
        }
        
        public Builder environmentState(EnvironmentState environmentState) {
            this.environmentState = environmentState;
            return this;
        }
        
        public Builder addEntity(EntityInfo entity) {
            this.nearbyEntities.add(entity);
            return this;
        }
        
        public Builder entities(List<EntityInfo> entities) {
            this.nearbyEntities.addAll(entities);
            return this;
        }
        
        public Builder addBlock(BlockInfo block) {
            this.nearbyBlocks.add(block);
            return this;
        }
        
        public Builder blocks(List<BlockInfo> blocks) {
            this.nearbyBlocks.addAll(blocks);
            return this;
        }
        
        public WorldState build() {
            return new WorldState(this);
        }
    }
}
