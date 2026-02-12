package org.lytharalab.csch.subconscious;

import org.lytharalab.csch.core.intent.Intent;
import org.lytharalab.csch.core.intent.IntentType;
import org.lytharalab.csch.core.skill.SkillCall;
import org.lytharalab.csch.core.skill.SkillPriority;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class IntentToSkillTranslator {
    
    private final SkillRegistry skillRegistry;
    
    public IntentToSkillTranslator(SkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
    }
    
    public List<SkillCall> translate(Intent intent, WorldState currentState) {
        List<SkillCall> skillCalls = new ArrayList<>();
        IntentType type = intent.getType();
        
        switch (type) {
            case NAVIGATE -> skillCalls = translateNavigate(intent, currentState);
            case MINE -> skillCalls = translateMine(intent, currentState);
            case COMBAT -> skillCalls = translateCombat(intent, currentState);
            case CRAFT -> skillCalls = translateCraft(intent, currentState);
            case BUILD -> skillCalls = translateBuild(intent, currentState);
            case SURVIVE -> skillCalls = translateSurvive(intent, currentState);
            case EXPLORE -> skillCalls = translateExplore(intent, currentState);
            case GATHER -> skillCalls = translateGather(intent, currentState);
            default -> skillCalls = translateGeneric(intent, currentState);
        }
        
        return skillCalls;
    }
    
    private List<SkillCall> translateNavigate(Intent intent, WorldState state) {
        List<SkillCall> calls = new ArrayList<>();
        
        String target = intent.getParameter("target");
        String action = intent.getParameter("action");
        
        if ("pathfind".equals(action)) {
            calls.add(SkillCall.builder()
                .skillName("NavigateTo")
                .parameter("target", target != null ? target : "destination")
                .parameter("speed", 1.0)
                .priority(SkillPriority.NORMAL)
                .intentId(intent.getId())
                .build());
        } else {
            calls.add(SkillCall.builder()
                .skillName("NavigateTo")
                .parameter("target", target != null ? target : "destination")
                .parameter("speed", intent.getParameter("speed", 1.0))
                .parameter("cautious", intent.getParameter("cautious", false))
                .priority(SkillPriority.NORMAL)
                .intentId(intent.getId())
                .build());
        }
        
        return calls;
    }
    
    private List<SkillCall> translateMine(Intent intent, WorldState state) {
        List<SkillCall> calls = new ArrayList<>();
        
        String resource = intent.getParameter("resource");
        Integer amount = intent.getParameter("amount", 1);
        
        calls.add(SkillCall.builder()
            .skillName("Mine")
            .parameter("resource", resource != null ? resource : "stone")
            .parameter("amount", amount)
            .priority(SkillPriority.NORMAL)
            .intentId(intent.getId())
            .build());
        
        return calls;
    }
    
    private List<SkillCall> translateCombat(Intent intent, WorldState state) {
        List<SkillCall> calls = new ArrayList<>();
        String action = intent.getParameter("action");
        
        if ("engage".equals(action)) {
            String target = intent.getParameter("target");
            Double distance = intent.getParameter("distance", 3.5);
            
            calls.add(SkillCall.builder()
                .skillName("CombatKite")
                .parameter("target", target != null ? target : "enemy")
                .parameter("distance", distance)
                .priority(SkillPriority.HIGH)
                .intentId(intent.getId())
                .build());
        } else {
            calls.add(SkillCall.builder()
                .skillName("AlignCrosshair")
                .parameter("target", intent.getParameter("target", "enemy"))
                .priority(SkillPriority.HIGH)
                .intentId(intent.getId())
                .build());
        }
        
        return calls;
    }
    
    private List<SkillCall> translateCraft(Intent intent, WorldState state) {
        List<SkillCall> calls = new ArrayList<>();
        
        String item = intent.getParameter("item");
        
        calls.add(SkillCall.builder()
            .skillName("Craft")
            .parameter("item", item != null ? item : "unknown")
            .priority(SkillPriority.NORMAL)
            .intentId(intent.getId())
            .build());
        
        return calls;
    }
    
    private List<SkillCall> translateBuild(Intent intent, WorldState state) {
        List<SkillCall> calls = new ArrayList<>();
        
        String action = intent.getParameter("action");
        
        if ("construct".equals(action)) {
            calls.add(SkillCall.builder()
                .skillName("PlaceTorch")
                .parameter("interval", 7)
                .priority(SkillPriority.NORMAL)
                .intentId(intent.getId())
                .build());
        } else {
            calls.add(SkillCall.builder()
                .skillName("Build")
                .parameter("structure", intent.getParameter("structure", "basic"))
                .priority(SkillPriority.NORMAL)
                .intentId(intent.getId())
                .build());
        }
        
        return calls;
    }
    
    private List<SkillCall> translateSurvive(Intent intent, WorldState state) {
        List<SkillCall> calls = new ArrayList<>();
        
        String action = intent.getParameter("action");
        
        if ("heal".equals(action)) {
            calls.add(SkillCall.builder()
                .skillName("Heal")
                .parameter("method", "food")
                .priority(SkillPriority.CRITICAL)
                .intentId(intent.getId())
                .build());
        } else if ("escape".equals(action)) {
            String threat = intent.getParameter("threat");
            calls.add(SkillCall.builder()
                .skillName("Escape")
                .parameter("threat", threat != null ? threat : "danger")
                .parameter("minDistance", 10.0)
                .priority(SkillPriority.INTERRUPT)
                .intentId(intent.getId())
                .build());
        } else {
            if (state != null && state.getPlayerState() != null) {
                PlayerState player = state.getPlayerState();
                if (player.getHealthRatio() < 0.3) {
                    calls.add(SkillCall.builder()
                        .skillName("Escape")
                        .parameter("threat", "low_health")
                        .priority(SkillPriority.INTERRUPT)
                        .intentId(intent.getId())
                        .build());
                }
            }
        }
        
        return calls;
    }
    
    private List<SkillCall> translateExplore(Intent intent, WorldState state) {
        List<SkillCall> calls = new ArrayList<>();
        
        String target = intent.getParameter("target");
        
        calls.add(SkillCall.builder()
            .skillName("NavigateTo")
            .parameter("target", target != null ? target : "unexplored")
            .parameter("speed", 0.8)
            .parameter("cautious", true)
            .priority(SkillPriority.LOW)
            .intentId(intent.getId())
            .build());
        
        return calls;
    }
    
    private List<SkillCall> translateGather(Intent intent, WorldState state) {
        List<SkillCall> calls = new ArrayList<>();
        
        String resource = intent.getParameter("target");
        if (resource == null) {
            resource = intent.getParameter("resource", "food");
        }
        
        calls.add(SkillCall.builder()
            .skillName("Mine")
            .parameter("resource", resource)
            .parameter("amount", intent.getParameter("amount", 1))
            .priority(SkillPriority.NORMAL)
            .intentId(intent.getId())
            .build());
        
        return calls;
    }
    
    private List<SkillCall> translateGeneric(Intent intent, WorldState state) {
        List<SkillCall> calls = new ArrayList<>();
        
        calls.add(SkillCall.builder()
            .skillName("GenericAction")
            .parameter("description", intent.getDescription())
            .priority(SkillPriority.NORMAL)
            .intentId(intent.getId())
            .build());
        
        return calls;
    }
}
