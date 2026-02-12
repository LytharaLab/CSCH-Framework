package org.lytharalab.csch.api;

import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.action.SafeMotorAction;
import org.lytharalab.csch.core.config.CSCHConfiguration;
import org.lytharalab.csch.core.event.CSCHEvent;
import org.lytharalab.csch.core.event.EventBus;
import org.lytharalab.csch.core.event.EventTypes;
import org.lytharalab.csch.core.intent.IntentGraph;
import org.lytharalab.csch.core.layer.*;
import org.lytharalab.csch.core.skill.SkillCall;
import org.lytharalab.csch.core.skill.SkillResult;
import org.lytharalab.csch.core.state.StateProvider;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.conscious.SimpleConsciousLayer;
import org.lytharalab.csch.subconscious.SimpleSubconsciousLayer;
import org.lytharalab.csch.cerebellum.SimpleCerebellumLayer;
import org.lytharalab.csch.safety.SimpleSafetyShield;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CSCHSystem {
    private static final Logger logger = LoggerFactory.getLogger(CSCHSystem.class);
    
    private final CSCHConfiguration configuration;
    private final ConsciousLayer consciousLayer;
    private final SubconsciousLayer subconsciousLayer;
    private final CerebellumLayer cerebellumLayer;
    private final SafetyShield safetyShield;
    private final StateProvider stateProvider;
    private final EventBus eventBus;
    
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicReference<String> currentGoal = new AtomicReference<>();
    private final AtomicReference<IntentGraph> currentIntentGraph = new AtomicReference<>();
    private final AtomicReference<SkillCall> currentSkillCall = new AtomicReference<>();
    
    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> controlLoop;
    
    private final BlockingQueue<SkillCall> skillQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<MotorAction> actionQueue = new LinkedBlockingQueue<>();
    
    public CSCHSystem(CSCHConfiguration configuration, StateProvider stateProvider, EventBus eventBus) {
        this.configuration = configuration;
        this.stateProvider = stateProvider;
        this.eventBus = eventBus;
        
        this.consciousLayer = new SimpleConsciousLayer();
        this.subconsciousLayer = new SimpleSubconsciousLayer();
        this.cerebellumLayer = new SimpleCerebellumLayer();
        this.safetyShield = new SimpleSafetyShield();
    }
    
    public CSCHSystem(StateProvider stateProvider) {
        this(CSCHConfiguration.defaultConfiguration(), stateProvider, new DefaultEventBus());
    }
    
    public void initialize() throws CSCHException {
        logger.info("Initializing CSCH System...");
        
        try {
            consciousLayer.initialize();
            subconsciousLayer.initialize();
            cerebellumLayer.initialize();
            safetyShield.initialize();
            
            executorService = Executors.newScheduledThreadPool(4);
            
            publishEvent(EventTypes.SYSTEM_INITIALIZED, "CSCH System initialized");
            
            logger.info("CSCH System initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize CSCH System", e);
            throw new CSCHException("CSCHSystem", CSCHException.ErrorCode.INITIALIZATION_FAILED,
                "Failed to initialize CSCH System", e);
        }
    }
    
    public void start() {
        if (running.compareAndSet(false, true)) {
            long motorIntervalMs = 1000 / configuration.getControlFrequencyHz();
            
            controlLoop = executorService.scheduleAtFixedRate(
                this::controlLoopIteration,
                0,
                motorIntervalMs,
                TimeUnit.MILLISECONDS
            );
            
            logger.info("CSCH System started with control frequency {} Hz", 
                configuration.getControlFrequencyHz());
        }
    }
    
    public void stop() {
        if (running.compareAndSet(true, false)) {
            if (controlLoop != null) {
                controlLoop.cancel(false);
            }
            
            publishEvent(EventTypes.SYSTEM_SHUTDOWN, "CSCH System stopped");
            logger.info("CSCH System stopped");
        }
    }
    
    public void shutdown() throws CSCHException {
        stop();
        
        try {
            if (executorService != null) {
                executorService.shutdown();
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            }
            
            consciousLayer.shutdown();
            subconsciousLayer.shutdown();
            cerebellumLayer.shutdown();
            safetyShield.shutdown();
            
            logger.info("CSCH System shutdown complete");
        } catch (Exception e) {
            throw new CSCHException("CSCHSystem", CSCHException.ErrorCode.SHUTDOWN_FAILED,
                "Failed to shutdown CSCH System", e);
        }
    }
    
    public void setGoal(String goal) {
        currentGoal.set(goal);
        logger.info("New goal set: {}", goal);
        
        WorldState currentState = stateProvider.getCurrentState();
        IntentGraph intentGraph = consciousLayer.generateIntentGraph(goal, currentState);
        currentIntentGraph.set(intentGraph);
        
        publishEvent(EventTypes.INTENT_GENERATED, goal);
    }
    
    private void controlLoopIteration() {
        if (!running.get()) {
            return;
        }
        
        try {
            WorldState currentState = stateProvider.getCurrentState();
            
            if (shouldUpdateIntent()) {
                updateIntentGraph(currentState);
            }
            
            if (currentSkillCall.get() == null) {
                selectNextSkill(currentState);
            }
            
            MotorAction motorAction = computeMotorAction(currentState);
            
            SafeMotorAction safeAction = safetyShield.filterAction(motorAction, currentState);
            
            if (safeAction.wasModified()) {
                publishEvent(EventTypes.ACTION_FILTERED, safeAction.getModificationReason());
            }
            
            actionQueue.offer(safeAction.getSafeAction());
            
            publishEvent(EventTypes.ACTION_COMPUTED, safeAction.getSafeAction());
            
        } catch (Exception e) {
            logger.error("Error in control loop", e);
            publishEvent(EventTypes.SYSTEM_ERROR, e.getMessage());
        }
    }
    
    private boolean shouldUpdateIntent() {
        IntentGraph graph = currentIntentGraph.get();
        if (graph == null) {
            return currentGoal.get() != null;
        }
        
        return consciousLayer.shouldIntervene(stateProvider.getCurrentState(), graph);
    }
    
    private void updateIntentGraph(WorldState currentState) {
        String goal = currentGoal.get();
        if (goal == null) {
            return;
        }
        
        IntentGraph currentGraph = currentIntentGraph.get();
        IntentGraph updatedGraph;
        
        if (currentGraph == null) {
            updatedGraph = consciousLayer.generateIntentGraph(goal, currentState);
        } else {
            updatedGraph = consciousLayer.updateIntentGraph(currentGraph, currentState);
        }
        
        currentIntentGraph.set(updatedGraph);
        publishEvent(EventTypes.INTENT_UPDATED, goal);
    }
    
    private void selectNextSkill(WorldState currentState) {
        IntentGraph graph = currentIntentGraph.get();
        if (graph == null) {
            return;
        }
        
        var rootNodes = graph.getRootNodes();
        if (rootNodes.isEmpty()) {
            return;
        }
        
        var rootNode = rootNodes.get(0);
        var intent = rootNode.getIntent();
        
        List<SkillCall> skillCalls = subconsciousLayer.translateIntent(intent, currentState);
        
        SkillCall selectedSkill = subconsciousLayer.selectNextSkill(currentState, skillCalls);
        
        if (selectedSkill != null) {
            currentSkillCall.set(selectedSkill);
            publishEvent(EventTypes.SKILL_CALLED, selectedSkill.getSkillName());
        }
    }
    
    private MotorAction computeMotorAction(WorldState currentState) {
        SkillCall skillCall = currentSkillCall.get();
        
        if (skillCall == null) {
            return MotorAction.idle();
        }
        
        return cerebellumLayer.computeMotorAction(skillCall, currentState);
    }
    
    public void reportSkillResult(SkillResult result) {
        SkillCall call = currentSkillCall.get();
        if (call == null) {
            return;
        }
        
        WorldState currentState = stateProvider.getCurrentState();
        subconsciousLayer.recordSkillExecution(call, result, currentState);
        
        if (result.isSuccess()) {
            publishEvent(EventTypes.SKILL_COMPLETED, call.getSkillName());
            currentSkillCall.set(null);
        } else {
            publishEvent(EventTypes.SKILL_FAILED, call.getSkillName() + ": " + result.getMessage());
            
            var recoveryCall = subconsciousLayer.handleSkillFailure(call, result, currentState);
            if (recoveryCall.isPresent()) {
                currentSkillCall.set(recoveryCall.get());
            } else {
                currentSkillCall.set(null);
            }
        }
    }
    
    public MotorAction getNextAction() {
        return actionQueue.poll();
    }
    
    public MotorAction getNextAction(long timeout, TimeUnit unit) throws InterruptedException {
        return actionQueue.poll(timeout, unit);
    }
    
    public ControlMetrics getControlMetrics() {
        return cerebellumLayer.getControlMetrics();
    }
    
    public String generateReflection() {
        return consciousLayer.generateReflection(
            stateProvider.getCurrentState(),
            currentIntentGraph.get()
        );
    }
    
    public boolean isRunning() {
        return running.get();
    }
    
    public String getCurrentGoal() {
        return currentGoal.get();
    }
    
    public IntentGraph getCurrentIntentGraph() {
        return currentIntentGraph.get();
    }
    
    public SkillCall getCurrentSkillCall() {
        return currentSkillCall.get();
    }
    
    public int getInterventionCount() {
        return subconsciousLayer.getInterventionCount();
    }
    
    public int getSafetyViolationCount() {
        return safetyShield.getViolationCount();
    }
    
    private void publishEvent(String type, Object data) {
        if (eventBus != null) {
            CSCHEvent event = CSCHEvent.builder()
                .type(type)
                .source("CSCHSystem")
                .data("message", data)
                .build();
            eventBus.publish(event);
        }
    }
    
    public ConsciousLayer getConsciousLayer() {
        return consciousLayer;
    }
    
    public SubconsciousLayer getSubconsciousLayer() {
        return subconsciousLayer;
    }
    
    public CerebellumLayer getCerebellumLayer() {
        return cerebellumLayer;
    }
    
    public SafetyShield getSafetyShield() {
        return safetyShield;
    }
}
