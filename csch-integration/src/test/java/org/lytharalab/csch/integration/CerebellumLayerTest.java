package org.lytharalab.csch.integration;

import org.junit.jupiter.api.*;
import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.layer.ControlMetrics;
import org.lytharalab.csch.core.skill.SkillCall;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.cerebellum.SimpleCerebellumLayer;
import org.lytharalab.csch.cerebellum.ActionSpace;
import org.lytharalab.csch.api.MockStateProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CerebellumLayerTest {
    
    private SimpleCerebellumLayer cerebellum;
    private MockStateProvider stateProvider;
    
    @BeforeEach
    void setUp() throws Exception {
        cerebellum = new SimpleCerebellumLayer();
        stateProvider = new MockStateProvider();
        cerebellum.initialize();
    }
    
    @AfterEach
    void tearDown() throws Exception {
        cerebellum.shutdown();
    }
    
    @Test
    void testInitialization() {
        assertTrue(cerebellum.isInitialized());
    }
    
    @Test
    void testMotorActionGeneration() {
        SkillCall skillCall = SkillCall.builder()
            .skillName("NavigateTo")
            .parameter("target", "destination")
            .parameter("speed", 1.0)
            .build();
        
        WorldState state = stateProvider.getCurrentState();
        
        MotorAction action = cerebellum.computeMotorAction(skillCall, state);
        
        assertNotNull(action);
        assertTrue(action.getMoveForward() >= 0 && action.getMoveForward() <= 1);
    }
    
    @Test
    void testActionSequenceGeneration() {
        SkillCall skillCall = SkillCall.builder()
            .skillName("NavigateTo")
            .parameter("target", "destination")
            .build();
        
        WorldState state = stateProvider.getCurrentState();
        
        List<MotorAction> actions = cerebellum.computeActionSequence(skillCall, state, 10);
        
        assertNotNull(actions);
        assertEquals(10, actions.size());
    }
    
    @Test
    void testControlMetrics() {
        ControlMetrics metrics = cerebellum.getControlMetrics();
        
        assertNotNull(metrics);
        assertTrue(metrics.getOverallQuality() >= 0);
    }
    
    @Test
    void testResetControlState() {
        SkillCall skillCall = SkillCall.builder()
            .skillName("NavigateTo")
            .build();
        
        cerebellum.computeMotorAction(skillCall, stateProvider.getCurrentState());
        
        cerebellum.resetControlState();
        
        ControlMetrics metrics = cerebellum.getControlMetrics();
        assertNotNull(metrics);
    }
    
    @Test
    void testActionSpaceConstraint() {
        ActionSpace actionSpace = ActionSpace.createDefault();
        
        MotorAction largeAction = MotorAction.builder()
            .moveForward(2.0)
            .strafe(-2.0)
            .yawRate(Math.PI)
            .build();
        
        double constrainedForward = actionSpace.clampMoveForward(largeAction.getMoveForward());
        double constrainedStrafe = actionSpace.clampStrafe(largeAction.getStrafe());
        double constrainedYaw = actionSpace.clampYawRate(largeAction.getYawRate());
        
        assertTrue(constrainedForward >= actionSpace.getMoveForwardMin() && 
                   constrainedForward <= actionSpace.getMoveForwardMax());
        assertTrue(constrainedStrafe >= actionSpace.getStrafeMin() && 
                   constrainedStrafe <= actionSpace.getStrafeMax());
        assertTrue(constrainedYaw >= actionSpace.getYawRateMin() && 
                   constrainedYaw <= actionSpace.getYawRateMax());
    }
    
    @Test
    void testIdleAction() {
        SkillCall skillCall = null;
        WorldState state = null;
        
        MotorAction action = cerebellum.computeMotorAction(skillCall, state);
        
        assertNotNull(action);
        assertEquals(0, action.getMoveForward(), 0.001);
        assertEquals(0, action.getStrafe(), 0.001);
    }
}
