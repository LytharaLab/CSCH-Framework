package org.lytharalab.csch.safety;

import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.action.SafeMotorAction;
import org.lytharalab.csch.core.layer.SafetyRule;
import org.lytharalab.csch.core.layer.SafetyViolation;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.safety.rules.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class SimpleSafetyShield extends AbstractSafetyShield {
    
    public SimpleSafetyShield() {
        addDefaultRules();
    }
    
    private void addDefaultRules() {
        addRule(new CliffAvoidanceRule());
        addRule(new HazardAvoidanceRule());
        addRule(new HealthProtectionRule());
        addRule(new ActionRateLimitRule());
        addRule(new CombatSafetyRule());
    }
    
    @Override
    protected void doInitialize() throws Exception {
        logger.info("Initializing SimpleSafetyShield with {} rules", rules.size());
    }
    
    @Override
    protected void doShutdown() throws Exception {
        logger.info("Shutting down SimpleSafetyShield");
        rules.clear();
    }
    
    @Override
    public SafeMotorAction filterAction(MotorAction action, WorldState currentState) {
        if (action == null) {
            return SafeMotorAction.unchanged(MotorAction.idle());
        }
        
        List<SafetyViolation> violations = checkViolations(action, currentState);
        
        if (violations.isEmpty()) {
            return SafeMotorAction.unchanged(action);
        }
        
        incrementViolationCount();
        
        SafetyViolation highestSeverity = violations.stream()
            .max(Comparator.comparingInt(v -> v.getSeverity().getLevel()))
            .orElse(null);
        
        if (highestSeverity != null) {
            logger.warn("Safety violation detected: {} - {}", 
                highestSeverity.getRuleName(), 
                highestSeverity.getDescription());
        }
        
        MotorAction correctedAction = applyCorrections(action, currentState, violations);
        
        return SafeMotorAction.modified(action, correctedAction, 
            highestSeverity != null ? highestSeverity.getDescription() : "Safety correction applied");
    }
    
    @Override
    public boolean isActionSafe(MotorAction action, WorldState currentState) {
        return checkViolations(action, currentState).isEmpty();
    }
    
    @Override
    public List<SafetyViolation> checkViolations(MotorAction action, WorldState currentState) {
        List<SafetyViolation> violations = new ArrayList<>();
        
        for (SafetyRule rule : rules.values()) {
            if (rule.isEnabled()) {
                SafetyViolation violation = rule.check(action, currentState);
                if (violation != null) {
                    violations.add(violation);
                }
            }
        }
        
        return violations;
    }
    
    private MotorAction applyCorrections(MotorAction originalAction, WorldState state, 
                                         List<SafetyViolation> violations) {
        MotorAction correctedAction = originalAction;
        
        List<SafetyViolation> sortedViolations = new ArrayList<>(violations);
        sortedViolations.sort((a, b) -> Integer.compare(
            b.getSeverity().getLevel(), 
            a.getSeverity().getLevel()));
        
        for (SafetyViolation violation : sortedViolations) {
            SafetyRule rule = rules.get(violation.getRuleId());
            if (rule != null) {
                correctedAction = rule.correct(correctedAction, state);
            }
        }
        
        return correctedAction;
    }
}
