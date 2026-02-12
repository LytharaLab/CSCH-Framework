package org.lytharalab.csch.core.layer;

import org.lytharalab.csch.core.intent.Intent;
import org.lytharalab.csch.core.intent.IntentGraph;
import org.lytharalab.csch.core.skill.SkillCall;
import org.lytharalab.csch.core.skill.SkillResult;
import org.lytharalab.csch.core.state.WorldState;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface SubconsciousLayer extends CSCHComponent {
    
    List<SkillCall> translateIntent(Intent intent, WorldState currentState);
    
    SkillCall selectNextSkill(WorldState currentState, List<SkillCall> pendingSkills);
    
    Optional<SkillCall> handleSkillFailure(SkillCall failedCall, SkillResult result, WorldState currentState);
    
    void recordSkillExecution(SkillCall call, SkillResult result, WorldState state);
    
    void learnFromExperience(SkillCall call, SkillResult result, WorldState beforeState, WorldState afterState);
    
    int getInterventionCount();
    
    void resetInterventionCount();
    
    default CompletableFuture<List<SkillCall>> translateIntentAsync(Intent intent, WorldState currentState) {
        return CompletableFuture.supplyAsync(() -> translateIntent(intent, currentState));
    }
}
