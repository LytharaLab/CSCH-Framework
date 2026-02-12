package org.lytharalab.csch.core.layer;

import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.state.WorldState;

import java.util.function.BiFunction;

public interface SafetyRule {
    
    String getId();
    
    String getName();
    
    String getDescription();
    
    SafetyViolation.ViolationSeverity getSeverity();
    
    boolean isEnabled();
    
    void setEnabled(boolean enabled);
    
    boolean violates(MotorAction action, WorldState state);
    
    MotorAction correct(MotorAction action, WorldState state);
    
    String getSuggestedAction(MotorAction action, WorldState state);
    
    default SafetyViolation check(MotorAction action, WorldState state) {
        if (!isEnabled()) {
            return null;
        }
        if (violates(action, state)) {
            return new SafetyViolation(
                getId(),
                getName(),
                getSeverity(),
                getDescription(),
                getSuggestedAction(action, state)
            );
        }
        return null;
    }
}
