package org.lytharalab.csch.core.layer;

import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.action.SafeMotorAction;
import org.lytharalab.csch.core.state.WorldState;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SafetyShield extends CSCHComponent {
    
    SafeMotorAction filterAction(MotorAction action, WorldState currentState);
    
    boolean isActionSafe(MotorAction action, WorldState currentState);
    
    List<SafetyViolation> checkViolations(MotorAction action, WorldState currentState);
    
    void addRule(SafetyRule rule);
    
    void removeRule(String ruleId);
    
    List<SafetyRule> getActiveRules();
    
    int getViolationCount();
    
    void resetViolationCount();
    
    default CompletableFuture<SafeMotorAction> filterActionAsync(MotorAction action, WorldState currentState) {
        return CompletableFuture.supplyAsync(() -> filterAction(action, currentState));
    }
}
