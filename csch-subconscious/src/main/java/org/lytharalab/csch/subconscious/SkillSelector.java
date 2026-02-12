package org.lytharalab.csch.subconscious;

import org.lytharalab.csch.core.skill.SkillCall;
import org.lytharalab.csch.core.skill.SkillPriority;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class SkillSelector {
    
    private final SkillExecutionHistory history;
    
    public SkillSelector(SkillExecutionHistory history) {
        this.history = history;
    }
    
    public SkillCall selectNextSkill(WorldState currentState, List<SkillCall> pendingSkills) {
        if (pendingSkills == null || pendingSkills.isEmpty()) {
            return null;
        }
        
        List<SkillCall> sortedSkills = new ArrayList<>(pendingSkills);
        
        sortedSkills.sort((a, b) -> {
            int priorityCompare = Integer.compare(
                b.getPriority().getValue(), 
                a.getPriority().getValue());
            if (priorityCompare != 0) {
                return priorityCompare;
            }
            
            int successRateCompare = Integer.compare(
                history.getSuccessRate(b.getSkillName()),
                history.getSuccessRate(a.getSkillName()));
            
            return successRateCompare;
        });
        
        for (SkillCall skill : sortedSkills) {
            if (canExecute(skill, currentState)) {
                return skill;
            }
        }
        
        return sortedSkills.isEmpty() ? null : sortedSkills.get(0);
    }
    
    private boolean canExecute(SkillCall skill, WorldState state) {
        if (state == null || state.getPlayerState() == null) {
            return true;
        }
        
        PlayerState player = state.getPlayerState();
        
        if (skill.getPriority() == SkillPriority.INTERRUPT) {
            return true;
        }
        
        if (player.getHealthRatio() < 0.2 && 
            !isSurvivalSkill(skill.getSkillName())) {
            return false;
        }
        
        return true;
    }
    
    private boolean isSurvivalSkill(String skillName) {
        return "Escape".equals(skillName) || 
               "Heal".equals(skillName) ||
               "NavigateTo".equals(skillName);
    }
    
    public List<SkillCall> filterExecutableSkills(WorldState currentState, List<SkillCall> pendingSkills) {
        List<SkillCall> executable = new ArrayList<>();
        
        for (SkillCall skill : pendingSkills) {
            if (canExecute(skill, currentState)) {
                executable.add(skill);
            }
        }
        
        return executable;
    }
    
    public SkillCall selectHighestPriority(List<SkillCall> skills) {
        if (skills == null || skills.isEmpty()) {
            return null;
        }
        
        return skills.stream()
            .max(Comparator.comparingInt(s -> s.getPriority().getValue()))
            .orElse(null);
    }
}
