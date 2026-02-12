package org.lytharalab.csch.integration;

import org.junit.jupiter.api.*;
import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.layer.SafetyRule;
import org.lytharalab.csch.core.layer.SafetyViolation;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.safety.SimpleSafetyShield;
import org.lytharalab.csch.safety.rules.*;
import org.lytharalab.csch.api.MockStateProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SafetyShieldTest {
    
    private SimpleSafetyShield safetyShield;
    private MockStateProvider stateProvider;
    
    @BeforeEach
    void setUp() {
        safetyShield = new SimpleSafetyShield();
        stateProvider = new MockStateProvider();
    }
    
    @Test
    void testInitialization() throws Exception {
        safetyShield.initialize();
        assertTrue(safetyShield.isInitialized());
        assertFalse(safetyShield.getActiveRules().isEmpty());
    }
    
    @Test
    void testDefaultRules() throws Exception {
        safetyShield.initialize();
        
        List<SafetyRule> rules = safetyShield.getActiveRules();
        
        assertFalse(rules.isEmpty());
        
        assertTrue(rules.stream().anyMatch(r -> r.getName().equals("CliffAvoidance")));
        assertTrue(rules.stream().anyMatch(r -> r.getName().equals("HazardAvoidance")));
        assertTrue(rules.stream().anyMatch(r -> r.getName().equals("HealthProtection")));
    }
    
    @Test
    void testSafeActionPasses() throws Exception {
        safetyShield.initialize();
        
        MotorAction safeAction = MotorAction.builder()
            .moveForward(0.5)
            .strafe(0)
            .yawRate(0.1)
            .build();
        
        WorldState state = stateProvider.getCurrentState();
        
        var result = safetyShield.filterAction(safeAction, state);
        
        assertNotNull(result);
        assertEquals(safeAction, result.getSafeAction());
    }
    
    @Test
    void testViolationDetection() throws Exception {
        safetyShield.initialize();
        
        stateProvider.updatePlayerHealth(5.0);
        
        MotorAction riskyAction = MotorAction.builder()
            .moveForward(1.0)
            .sprint(true)
            .attack(true)
            .build();
        
        WorldState state = stateProvider.getCurrentState();
        
        List<SafetyViolation> violations = safetyShield.checkViolations(riskyAction, state);
        
        assertFalse(violations.isEmpty());
    }
    
    @Test
    void testActionCorrection() throws Exception {
        safetyShield.initialize();
        
        stateProvider.updatePlayerHealth(3.0);
        
        MotorAction riskyAction = MotorAction.builder()
            .moveForward(1.0)
            .sprint(true)
            .build();
        
        WorldState state = stateProvider.getCurrentState();
        
        var result = safetyShield.filterAction(riskyAction, state);
        
        assertTrue(result.wasModified());
        assertFalse(result.getSafeAction().isSprint());
    }
    
    @Test
    void testAddRemoveRule() throws Exception {
        safetyShield.initialize();
        
        int initialCount = safetyShield.getActiveRules().size();
        
        SafetyRule customRule = new SafetyRule() {
            private final String id = java.util.UUID.randomUUID().toString();
            private boolean enabled = true;
            
            @Override public String getId() { return id; }
            @Override public String getName() { return "CustomRule"; }
            @Override public String getDescription() { return "Test rule"; }
            @Override public SafetyViolation.ViolationSeverity getSeverity() { 
                return SafetyViolation.ViolationSeverity.WARNING; 
            }
            @Override public boolean isEnabled() { return enabled; }
            @Override public void setEnabled(boolean enabled) { this.enabled = enabled; }
            @Override public boolean violates(MotorAction action, WorldState state) { return false; }
            @Override public MotorAction correct(MotorAction action, WorldState state) { return action; }
            @Override public String getSuggestedAction(MotorAction action, WorldState state) { return ""; }
        };
        
        safetyShield.addRule(customRule);
        assertEquals(initialCount + 1, safetyShield.getActiveRules().size());
        
        safetyShield.removeRule(customRule.getId());
        assertEquals(initialCount, safetyShield.getActiveRules().size());
    }
    
    @Test
    void testViolationCount() throws Exception {
        safetyShield.initialize();
        
        assertEquals(0, safetyShield.getViolationCount());
        
        stateProvider.updatePlayerHealth(2.0);
        
        MotorAction riskyAction = MotorAction.builder()
            .sprint(true)
            .attack(true)
            .build();
        
        safetyShield.filterAction(riskyAction, stateProvider.getCurrentState());
        
        assertTrue(safetyShield.getViolationCount() > 0);
        
        safetyShield.resetViolationCount();
        assertEquals(0, safetyShield.getViolationCount());
    }
}
