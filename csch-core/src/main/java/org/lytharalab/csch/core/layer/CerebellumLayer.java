package org.lytharalab.csch.core.layer;

import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.skill.SkillCall;
import org.lytharalab.csch.core.state.WorldState;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CerebellumLayer extends CSCHComponent {
    
    MotorAction computeMotorAction(SkillCall skillCall, WorldState currentState);
    
    List<MotorAction> computeActionSequence(SkillCall skillCall, WorldState currentState, int horizon);
    
    void updateFromFeedback(WorldState previousState, MotorAction action, WorldState currentState, double reward);
    
    ControlMetrics getControlMetrics();
    
    void resetControlState();
    
    boolean isConverged();
    
    default CompletableFuture<MotorAction> computeMotorActionAsync(SkillCall skillCall, WorldState currentState) {
        return CompletableFuture.supplyAsync(() -> computeMotorAction(skillCall, currentState));
    }
}
