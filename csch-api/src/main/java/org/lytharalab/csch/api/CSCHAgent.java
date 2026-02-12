package org.lytharalab.csch.api;

import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.config.CSCHConfiguration;
import org.lytharalab.csch.core.event.EventBus;
import org.lytharalab.csch.core.intent.IntentGraph;
import org.lytharalab.csch.core.layer.ControlMetrics;
import org.lytharalab.csch.core.layer.CSCHException;
import org.lytharalab.csch.core.skill.SkillCall;
import org.lytharalab.csch.core.skill.SkillResult;
import org.lytharalab.csch.core.state.StateProvider;
import org.lytharalab.csch.core.state.WorldState;

import java.util.concurrent.TimeUnit;

public class CSCHAgent {
    
    private final CSCHSystem system;
    private final StateProvider stateProvider;
    private volatile boolean executing = false;
    
    public CSCHAgent(StateProvider stateProvider, CSCHConfiguration configuration, EventBus eventBus) {
        this.stateProvider = stateProvider;
        this.system = new CSCHSystem(configuration, stateProvider, eventBus);
    }
    
    public CSCHAgent(StateProvider stateProvider) {
        this.stateProvider = stateProvider;
        this.system = new CSCHSystem(stateProvider);
    }
    
    public void initialize() throws CSCHException {
        system.initialize();
    }
    
    public void start() {
        system.start();
        executing = true;
    }
    
    public void stop() {
        executing = false;
        system.stop();
    }
    
    public void shutdown() throws CSCHException {
        system.shutdown();
    }
    
    public void executeGoal(String goal) {
        system.setGoal(goal);
    }
    
    public MotorAction getAction() {
        return system.getNextAction();
    }
    
    public MotorAction getAction(long timeout, TimeUnit unit) throws InterruptedException {
        return system.getNextAction(timeout, unit);
    }
    
    public void reportResult(SkillResult result) {
        system.reportSkillResult(result);
    }
    
    public void reportSuccess() {
        SkillCall current = system.getCurrentSkillCall();
        if (current != null) {
            reportResult(SkillResult.success(current.getId(), current.getSkillName(), 
                java.time.Duration.ofMillis(100)));
        }
    }
    
    public void reportFailure(String message) {
        SkillCall current = system.getCurrentSkillCall();
        if (current != null) {
            reportResult(SkillResult.failure(current.getId(), current.getSkillName(), message));
        }
    }
    
    public WorldState getCurrentState() {
        return stateProvider.getCurrentState();
    }
    
    public ControlMetrics getMetrics() {
        return system.getControlMetrics();
    }
    
    public String getReflection() {
        return system.generateReflection();
    }
    
    public String getCurrentGoal() {
        return system.getCurrentGoal();
    }
    
    public IntentGraph getCurrentIntentGraph() {
        return system.getCurrentIntentGraph();
    }
    
    public SkillCall getCurrentSkill() {
        return system.getCurrentSkillCall();
    }
    
    public boolean isRunning() {
        return system.isRunning();
    }
    
    public boolean isExecuting() {
        return executing;
    }
    
    public int getInterventionCount() {
        return system.getInterventionCount();
    }
    
    public int getSafetyViolationCount() {
        return system.getSafetyViolationCount();
    }
    
    public CSCHSystem getSystem() {
        return system;
    }
}
