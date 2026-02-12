package org.lytharalab.csch.api;

import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.state.*;

import java.util.List;
import java.util.ArrayList;

public class MockStateProvider implements StateProvider {
    
    private WorldState currentState;
    
    public MockStateProvider() {
        this.currentState = createDefaultState();
    }
    
    @Override
    public WorldState getCurrentState() {
        return currentState;
    }
    
    @Override
    public PlayerState getPlayerState() {
        return currentState != null ? currentState.getPlayerState() : null;
    }
    
    @Override
    public EnvironmentState getEnvironmentState() {
        return currentState != null ? currentState.getEnvironmentState() : null;
    }
    
    public void updatePlayerPosition(double x, double y, double z) {
        PlayerState currentPlayer = currentState.getPlayerState();
        if (currentPlayer == null) {
            return;
        }
        
        PlayerState newPlayerState = PlayerState.builder()
            .position(x, y, z)
            .velocity(currentPlayer.getVelocityX(), currentPlayer.getVelocityY(), currentPlayer.getVelocityZ())
            .rotation(currentPlayer.getYaw(), currentPlayer.getPitch())
            .health(currentPlayer.getHealth(), currentPlayer.getMaxHealth())
            .hunger(currentPlayer.getHunger(), currentPlayer.getMaxHunger())
            .onGround(currentPlayer.isOnGround())
            .inWater(currentPlayer.isInWater())
            .sprinting(currentPlayer.isSprinting())
            .build();
        
        currentState = WorldState.builder()
            .playerState(newPlayerState)
            .environmentState(currentState.getEnvironmentState())
            .entities(currentState.getNearbyEntities())
            .blocks(currentState.getNearbyBlocks())
            .build();
    }
    
    public void updatePlayerHealth(double health) {
        PlayerState currentPlayer = currentState.getPlayerState();
        if (currentPlayer == null) {
            return;
        }
        
        PlayerState newPlayerState = PlayerState.builder()
            .position(currentPlayer.getPositionX(), currentPlayer.getPositionY(), currentPlayer.getPositionZ())
            .velocity(currentPlayer.getVelocityX(), currentPlayer.getVelocityY(), currentPlayer.getVelocityZ())
            .rotation(currentPlayer.getYaw(), currentPlayer.getPitch())
            .health(health, currentPlayer.getMaxHealth())
            .hunger(currentPlayer.getHunger(), currentPlayer.getMaxHunger())
            .onGround(currentPlayer.isOnGround())
            .inWater(currentPlayer.isInWater())
            .sprinting(currentPlayer.isSprinting())
            .build();
        
        currentState = WorldState.builder()
            .playerState(newPlayerState)
            .environmentState(currentState.getEnvironmentState())
            .entities(currentState.getNearbyEntities())
            .blocks(currentState.getNearbyBlocks())
            .build();
    }
    
    public void updatePlayerRotation(float yaw, float pitch) {
        PlayerState currentPlayer = currentState.getPlayerState();
        if (currentPlayer == null) {
            return;
        }
        
        PlayerState newPlayerState = PlayerState.builder()
            .position(currentPlayer.getPositionX(), currentPlayer.getPositionY(), currentPlayer.getPositionZ())
            .velocity(currentPlayer.getVelocityX(), currentPlayer.getVelocityY(), currentPlayer.getVelocityZ())
            .rotation(yaw, pitch)
            .health(currentPlayer.getHealth(), currentPlayer.getMaxHealth())
            .hunger(currentPlayer.getHunger(), currentPlayer.getMaxHunger())
            .onGround(currentPlayer.isOnGround())
            .inWater(currentPlayer.isInWater())
            .sprinting(currentPlayer.isSprinting())
            .build();
        
        currentState = WorldState.builder()
            .playerState(newPlayerState)
            .environmentState(currentState.getEnvironmentState())
            .entities(currentState.getNearbyEntities())
            .blocks(currentState.getNearbyBlocks())
            .build();
    }
    
    public void addNearbyEntity(EntityInfo entity) {
        currentState = WorldState.builder()
            .playerState(currentState.getPlayerState())
            .environmentState(currentState.getEnvironmentState())
            .entities(currentState.getNearbyEntities())
            .addEntity(entity)
            .blocks(currentState.getNearbyBlocks())
            .build();
    }
    
    public void addNearbyBlock(BlockInfo block) {
        currentState = WorldState.builder()
            .playerState(currentState.getPlayerState())
            .environmentState(currentState.getEnvironmentState())
            .entities(currentState.getNearbyEntities())
            .blocks(currentState.getNearbyBlocks())
            .addBlock(block)
            .build();
    }
    
    public void setState(WorldState state) {
        this.currentState = state;
    }
    
    private WorldState createDefaultState() {
        PlayerState playerState = PlayerState.builder()
            .position(0, 64, 0)
            .velocity(0, 0, 0)
            .rotation(0, 0)
            .health(20, 20)
            .hunger(20, 20)
            .onGround(true)
            .inWater(false)
            .sprinting(false)
            .build();
        
        EnvironmentState environmentState = EnvironmentState.builder()
            .worldTime(1000)
            .dimension("overworld")
            .biome("plains")
            .lightLevel(15)
            .raining(false)
            .thundering(false)
            .build();
        
        return WorldState.builder()
            .playerState(playerState)
            .environmentState(environmentState)
            .build();
    }
}
