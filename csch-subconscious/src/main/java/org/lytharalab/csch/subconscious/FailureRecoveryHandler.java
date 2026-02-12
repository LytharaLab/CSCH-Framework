package org.lytharalab.csch.subconscious;

import org.lytharalab.csch.core.skill.SkillCall;
import org.lytharalab.csch.core.skill.SkillResult;
import org.lytharalab.csch.core.skill.SkillResultStatus;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

public class FailureRecoveryHandler {
    
    private final SkillExecutionHistory history;
    private final Map<String, Integer> failureCounts = new HashMap<>();
    private final int maxRetries = 3;
    
    public FailureRecoveryHandler(SkillExecutionHistory history) {
        this.history = history;
    }
    
    public Optional<SkillCall> handleFailure(SkillCall failedCall, SkillResult result, 
                                             WorldState currentState) {
        String skillName = failedCall.getSkillName();
        int failures = failureCounts.getOrDefault(skillName, 0) + 1;
        failureCounts.put(skillName, failures);
        
        if (failures >= maxRetries) {
            return createAlternativeSkill(failedCall, currentState);
        }
        
        if (result.getStatus() == SkillResultStatus.TIMEOUT) {
            return createRetryWithAdjustedParams(failedCall, currentState);
        }
        
        if (result.getStatus() == SkillResultStatus.BLOCKED) {
            return createFallbackSkill(failedCall, currentState);
        }
        
        return Optional.of(failedCall);
    }
    
    private Optional<SkillCall> createAlternativeSkill(SkillCall failedCall, WorldState state) {
        String skillName = failedCall.getSkillName();
        
        return switch (skillName) {
            case "NavigateTo" -> Optional.of(SkillCall.builder()
                .skillName("NavigateTo")
                .parameter("target", failedCall.getParameter("target"))
                .parameter("speed", 0.5)
                .parameter("cautious", true)
                .priority(failedCall.getPriority())
                .intentId(failedCall.getIntentId())
                .build());
            
            case "Mine" -> Optional.of(SkillCall.builder()
                .skillName("NavigateTo")
                .parameter("target", "alternative_location")
                .priority(failedCall.getPriority())
                .intentId(failedCall.getIntentId())
                .build());
            
            case "CombatKite" -> Optional.of(SkillCall.builder()
                .skillName("Escape")
                .parameter("threat", "enemy")
                .parameter("minDistance", 15.0)
                .priority(failedCall.getPriority())
                .intentId(failedCall.getIntentId())
                .build());
            
            default -> Optional.empty();
        };
    }
    
    private Optional<SkillCall> createRetryWithAdjustedParams(SkillCall failedCall, WorldState state) {
        Map<String, Object> newParams = new HashMap<>(failedCall.getParameters());
        
        if (newParams.containsKey("speed")) {
            double currentSpeed = (Double) newParams.get("speed");
            newParams.put("speed", currentSpeed * 0.8);
        }
        
        if (newParams.containsKey("distance")) {
            double currentDist = (Double) newParams.get("distance");
            newParams.put("distance", currentDist * 1.2);
        }
        
        newParams.put("retry", true);
        
        return Optional.of(SkillCall.builder()
            .skillName(failedCall.getSkillName())
            .parameters(newParams)
            .priority(failedCall.getPriority())
            .intentId(failedCall.getIntentId())
            .build());
    }
    
    private Optional<SkillCall> createFallbackSkill(SkillCall failedCall, WorldState state) {
        if (state != null && state.getPlayerState() != null) {
            PlayerState player = state.getPlayerState();
            
            if (player.getHealthRatio() < 0.5) {
                return Optional.of(SkillCall.builder()
                    .skillName("Escape")
                    .parameter("threat", "blocked")
                    .priority(failedCall.getPriority())
                    .intentId(failedCall.getIntentId())
                    .build());
            }
        }
        
        return Optional.of(SkillCall.builder()
            .skillName("NavigateTo")
            .parameter("target", "safe_location")
            .parameter("cautious", true)
            .priority(failedCall.getPriority())
            .intentId(failedCall.getIntentId())
            .build());
    }
    
    public void resetFailureCount(String skillName) {
        failureCounts.remove(skillName);
    }
    
    public void resetAllFailureCounts() {
        failureCounts.clear();
    }
    
    public int getFailureCount(String skillName) {
        return failureCounts.getOrDefault(skillName, 0);
    }
}
