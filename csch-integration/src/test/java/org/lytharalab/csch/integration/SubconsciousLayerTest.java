package org.lytharalab.csch.integration;

import org.junit.jupiter.api.*;
import org.lytharalab.csch.core.intent.Intent;
import org.lytharalab.csch.core.intent.IntentGraph;
import org.lytharalab.csch.core.intent.IntentType;
import org.lytharalab.csch.core.skill.SkillCall;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.subconscious.SimpleSubconsciousLayer;
import org.lytharalab.csch.api.MockStateProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubconsciousLayerTest {
    
    private SimpleSubconsciousLayer subconscious;
    private MockStateProvider stateProvider;
    
    @BeforeEach
    void setUp() throws Exception {
        subconscious = new SimpleSubconsciousLayer();
        stateProvider = new MockStateProvider();
        subconscious.initialize();
    }
    
    @AfterEach
    void tearDown() throws Exception {
        subconscious.shutdown();
    }
    
    @Test
    void testInitialization() {
        assertTrue(subconscious.isInitialized());
        assertNotNull(subconscious.getSkillRegistry());
    }
    
    @Test
    void testNavigateIntentTranslation() {
        Intent intent = Intent.builder()
            .description("导航到村庄")
            .type(IntentType.NAVIGATE)
            .parameter("target", "village")
            .parameter("speed", 1.0)
            .build();
        
        WorldState state = stateProvider.getCurrentState();
        
        List<SkillCall> skillCalls = subconscious.translateIntent(intent, state);
        
        assertFalse(skillCalls.isEmpty());
        
        SkillCall firstCall = skillCalls.get(0);
        assertNotNull(firstCall.getSkillName());
    }
    
    @Test
    void testMineIntentTranslation() {
        Intent intent = Intent.builder()
            .description("挖掘铁矿")
            .type(IntentType.MINE)
            .parameter("resource", "iron_ore")
            .parameter("amount", 12)
            .build();
        
        WorldState state = stateProvider.getCurrentState();
        
        List<SkillCall> skillCalls = subconscious.translateIntent(intent, state);
        
        assertFalse(skillCalls.isEmpty());
        
        boolean hasMineSkill = skillCalls.stream()
            .anyMatch(c -> c.getSkillName().equals("Mine"));
        assertTrue(hasMineSkill);
    }
    
    @Test
    void testCombatIntentTranslation() {
        Intent intent = Intent.builder()
            .description("攻击僵尸")
            .type(IntentType.COMBAT)
            .parameter("target", "zombie")
            .parameter("distance", 3.5)
            .build();
        
        WorldState state = stateProvider.getCurrentState();
        
        List<SkillCall> skillCalls = subconscious.translateIntent(intent, state);
        
        assertFalse(skillCalls.isEmpty());
    }
    
    @Test
    void testSkillSelection() {
        Intent intent = Intent.builder()
            .description("测试技能选择")
            .type(IntentType.NAVIGATE)
            .build();
        
        WorldState state = stateProvider.getCurrentState();
        
        List<SkillCall> skillCalls = subconscious.translateIntent(intent, state);
        
        SkillCall selected = subconscious.selectNextSkill(state, skillCalls);
        
        if (!skillCalls.isEmpty()) {
            assertNotNull(selected);
        }
    }
    
    @Test
    void testInterventionCount() {
        assertEquals(0, subconscious.getInterventionCount());
        
        subconscious.resetInterventionCount();
        assertEquals(0, subconscious.getInterventionCount());
    }
    
    @Test
    void testSkillRegistry() {
        var registry = subconscious.getSkillRegistry();
        
        assertTrue(registry.hasSkill("NavigateTo"));
        assertTrue(registry.hasSkill("Mine"));
        assertTrue(registry.hasSkill("CombatKite"));
        assertTrue(registry.hasSkill("Escape"));
    }
}
