package org.lytharalab.csch.subconscious;

import org.lytharalab.csch.core.intent.Intent;
import org.lytharalab.csch.core.skill.SkillCall;
import org.lytharalab.csch.core.skill.SkillResult;
import org.lytharalab.csch.core.state.WorldState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class SimpleSubconsciousLayer extends AbstractSubconsciousLayer {
    
    private final SkillRegistry skillRegistry;
    private final IntentToSkillTranslator translator;
    private final SkillSelector selector;
    private final SkillExecutionHistory history;
    private final FailureRecoveryHandler recoveryHandler;
    
    public SimpleSubconsciousLayer() {
        this.skillRegistry = SkillRegistry.createDefaultRegistry();
        this.history = new SkillExecutionHistory(100);
        this.translator = new IntentToSkillTranslator(skillRegistry);
        this.selector = new SkillSelector(history);
        this.recoveryHandler = new FailureRecoveryHandler(history);
    }
    
    public SimpleSubconsciousLayer(SkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
        this.history = new SkillExecutionHistory(100);
        this.translator = new IntentToSkillTranslator(skillRegistry);
        this.selector = new SkillSelector(history);
        this.recoveryHandler = new FailureRecoveryHandler(history);
    }
    
    @Override
    protected void doInitialize() throws Exception {
        logger.info("Initializing SimpleSubconsciousLayer with {} skills", skillRegistry.size());
    }
    
    @Override
    protected void doShutdown() throws Exception {
        logger.info("Shutting down SimpleSubconsciousLayer");
        history.clear();
    }
    
    @Override
    public List<SkillCall> translateIntent(Intent intent, WorldState currentState) {
        logger.debug("Translating intent: {}", intent.getDescription());
        
        List<SkillCall> skillCalls = translator.translate(intent, currentState);
        
        logger.debug("Translated to {} skill calls", skillCalls.size());
        
        return skillCalls;
    }
    
    @Override
    public SkillCall selectNextSkill(WorldState currentState, List<SkillCall> pendingSkills) {
        SkillCall selected = selector.selectNextSkill(currentState, pendingSkills);
        
        if (selected != null) {
            logger.debug("Selected skill: {}", selected.getSkillName());
        }
        
        return selected;
    }
    
    @Override
    public Optional<SkillCall> handleSkillFailure(SkillCall failedCall, SkillResult result, 
                                                   WorldState currentState) {
        logger.warn("Handling skill failure: {} - {}", failedCall.getSkillName(), result.getStatus());
        
        incrementInterventionCount();
        
        return recoveryHandler.handleFailure(failedCall, result, currentState);
    }
    
    @Override
    public void recordSkillExecution(SkillCall call, SkillResult result, WorldState state) {
        history.record(call, result, null, state);
        
        if (!result.isSuccess()) {
            logger.debug("Recorded failed execution of skill: {}", call.getSkillName());
        }
    }
    
    @Override
    public void learnFromExperience(SkillCall call, SkillResult result, 
                                   WorldState beforeState, WorldState afterState) {
        history.record(call, result, beforeState, afterState);
        
        if (result.isSuccess()) {
            recoveryHandler.resetFailureCount(call.getSkillName());
        }
        
        logger.debug("Learned from experience: {} - success rate: {}%", 
            call.getSkillName(), history.getSuccessRate(call.getSkillName()));
    }
    
    public SkillRegistry getSkillRegistry() {
        return skillRegistry;
    }
    
    public SkillExecutionHistory getHistory() {
        return history;
    }
}
