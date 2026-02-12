package org.lytharalab.csch.integration;

import org.lytharalab.csch.api.*;
import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.config.CSCHConfiguration;
import org.lytharalab.csch.core.layer.ControlMetrics;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;

import java.util.concurrent.TimeUnit;

public class SimpleAgentExample {
    
    public static void main(String[] args) {
        System.out.println("=== CSCH Framework Simple Agent Example ===\n");
        
        MockStateProvider stateProvider = new MockStateProvider();
        
        CSCHConfiguration config = CSCHConfiguration.builder()
            .controlFrequencyHz(20)
            .safetyShieldEnabled(true)
            .openclEnabled(false)
            .build();
        
        CSCHAgent agent = CSCHFactory.createAgent(stateProvider, config);
        
        try {
            System.out.println("Initializing agent...");
            agent.initialize();
            
            System.out.println("Starting agent...");
            agent.start();
            
            System.out.println("Setting goal: 去挖铁矿");
            agent.executeGoal("去挖铁矿");
            
            Thread.sleep(1000);
            
            System.out.println("\n--- Running for 5 seconds ---\n");
            
            for (int i = 0; i < 100; i++) {
                MotorAction action = agent.getAction(100, TimeUnit.MILLISECONDS);
                if (action != null) {
                    System.out.printf("Step %d: Action [forward=%.2f, strafe=%.2f, yaw=%.2f, pitch=%.2f]%n",
                        i + 1,
                        action.getMoveForward(),
                        action.getStrafe(),
                        action.getYawRate(),
                        action.getPitchRate()
                    );
                    
                    simulateAction(stateProvider, action);
                    
                    if (i % 10 == 0) {
                        agent.reportSuccess();
                    }
                }
            }
            
            System.out.println("\n--- Final State ---");
            WorldState state = agent.getCurrentState();
            if (state != null && state.getPlayerState() != null) {
                PlayerState player = state.getPlayerState();
                System.out.printf("Position: (%.1f, %.1f, %.1f)%n",
                    player.getPositionX(), player.getPositionY(), player.getPositionZ());
                System.out.printf("Health: %.1f/%.1f%n", player.getHealth(), player.getMaxHealth());
            }
            
            ControlMetrics metrics = agent.getMetrics();
            System.out.printf("Control Quality: %.2f%n", metrics.getOverallQuality());
            System.out.printf("Aim Error: %.4f%n", metrics.getAimError());
            System.out.printf("Smoothness: %.2f%n", metrics.getSmoothness());
            
            System.out.println("\n--- Reflection ---");
            System.out.println(agent.getReflection());
            
            System.out.println("\nIntervention count: " + agent.getInterventionCount());
            System.out.println("Safety violations: " + agent.getSafetyViolationCount());
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\nStopping agent...");
            agent.stop();
            
            try {
                agent.shutdown();
            } catch (Exception e) {
                System.err.println("Shutdown error: " + e.getMessage());
            }
            
            System.out.println("Agent shutdown complete.");
        }
    }
    
    private static void simulateAction(MockStateProvider stateProvider, MotorAction action) {
        PlayerState current = stateProvider.getCurrentState().getPlayerState();
        if (current == null) return;
        
        double newX = current.getPositionX() + action.getMoveForward() * 0.5;
        double newZ = current.getPositionZ() + action.getStrafe() * 0.5;
        float newYaw = (float) (current.getYaw() + Math.toDegrees(action.getYawRate()));
        float newPitch = (float) (current.getPitch() + Math.toDegrees(action.getPitchRate()));
        
        stateProvider.updatePlayerPosition(newX, current.getPositionY(), newZ);
        stateProvider.updatePlayerRotation(newYaw, newPitch);
    }
}
