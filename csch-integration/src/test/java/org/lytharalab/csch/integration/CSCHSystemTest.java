package org.lytharalab.csch.integration;

import org.junit.jupiter.api.*;
import org.lytharalab.csch.api.*;
import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.config.CSCHConfiguration;
import org.lytharalab.csch.core.intent.IntentGraph;
import org.lytharalab.csch.core.layer.CSCHException;
import org.lytharalab.csch.core.skill.SkillCall;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CSCHSystemTest {
    
    private static MockStateProvider stateProvider;
    private static CSCHSystem system;
    
    @BeforeAll
    static void setUpClass() throws CSCHException {
        stateProvider = new MockStateProvider();
        
        CSCHConfiguration config = CSCHConfiguration.builder()
            .controlFrequencyHz(60)
            .safetyShieldEnabled(true)
            .build();
        
        system = CSCHFactory.createSystem(stateProvider, config);
        system.initialize();
    }
    
    @AfterAll
    static void tearDownClass() throws CSCHException {
        if (system != null) {
            system.shutdown();
        }
    }
    
    @Test
    @Order(1)
    void testInitialization() {
        assertTrue(system.getConsciousLayer().isInitialized());
        assertTrue(system.getSubconsciousLayer().isInitialized());
        assertTrue(system.getCerebellumLayer().isInitialized());
        assertTrue(system.getSafetyShield().isInitialized());
    }
    
    @Test
    @Order(2)
    void testStartStop() {
        assertFalse(system.isRunning());
        
        system.start();
        assertTrue(system.isRunning());
        
        system.stop();
        assertFalse(system.isRunning());
    }
    
    @Test
    @Order(3)
    void testSetGoal() throws InterruptedException {
        system.start();
        
        system.setGoal("导航到村庄");
        
        Thread.sleep(500);
        
        assertNotNull(system.getCurrentGoal());
        assertEquals("导航到村庄", system.getCurrentGoal());
        
        IntentGraph graph = system.getCurrentIntentGraph();
        assertNotNull(graph);
        
        system.stop();
    }
    
    @Test
    @Order(4)
    void testActionGeneration() throws InterruptedException {
        system.start();
        system.setGoal("挖掘铁矿");
        
        Thread.sleep(200);
        
        MotorAction action = system.getNextAction(500, TimeUnit.MILLISECONDS);
        
        if (action != null) {
            assertTrue(action.getMoveForward() >= -1 && action.getMoveForward() <= 1);
            assertTrue(action.getStrafe() >= -1 && action.getStrafe() <= 1);
        }
        
        system.stop();
    }
    
    @Test
    @Order(5)
    void testSafetyShield() {
        MotorAction dangerousAction = MotorAction.builder()
            .moveForward(1.0)
            .sprint(true)
            .build();
        
        boolean isSafe = system.getSafetyShield().isActionSafe(dangerousAction, stateProvider.getCurrentState());
        
        var safeAction = system.getSafetyShield().filterAction(dangerousAction, stateProvider.getCurrentState());
        
        assertNotNull(safeAction);
        assertNotNull(safeAction.getSafeAction());
    }
    
    @Test
    @Order(6)
    void testControlMetrics() {
        var metrics = system.getControlMetrics();
        
        assertNotNull(metrics);
        assertTrue(metrics.getOverallQuality() >= 0 && metrics.getOverallQuality() <= 1);
    }
    
    @Test
    @Order(7)
    void testReflection() {
        String reflection = system.generateReflection();
        
        assertNotNull(reflection);
        assertTrue(reflection.contains("执行摘要") || reflection.contains("当前状态"));
    }
}
