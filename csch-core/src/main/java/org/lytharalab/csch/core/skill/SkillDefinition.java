package org.lytharalab.csch.core.skill;

import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class SkillDefinition {
    private final String name;
    private final String description;
    private final SkillCategory category;
    private final List<SkillParameter> parameters;
    private final Map<String, Object> defaultParameters;
    private final Duration estimatedDuration;
    private final boolean interruptible;
    private final List<String> requiredSkills;
    private final List<String> incompatibleSkills;
    
    private SkillDefinition(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.category = builder.category != null ? builder.category : SkillCategory.GENERAL;
        this.parameters = Collections.unmodifiableList(new ArrayList<>(builder.parameters));
        this.defaultParameters = Collections.unmodifiableMap(new HashMap<>(builder.defaultParameters));
        this.estimatedDuration = builder.estimatedDuration;
        this.interruptible = builder.interruptible;
        this.requiredSkills = Collections.unmodifiableList(new ArrayList<>(builder.requiredSkills));
        this.incompatibleSkills = Collections.unmodifiableList(new ArrayList<>(builder.incompatibleSkills));
    }
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public SkillCategory getCategory() { return category; }
    public List<SkillParameter> getParameters() { return parameters; }
    public Map<String, Object> getDefaultParameters() { return defaultParameters; }
    public Duration getEstimatedDuration() { return estimatedDuration; }
    public boolean isInterruptible() { return interruptible; }
    public List<String> getRequiredSkills() { return requiredSkills; }
    public List<String> getIncompatibleSkills() { return incompatibleSkills; }
    
    public SkillParameter getParameter(String name) {
        return parameters.stream()
            .filter(p -> p.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
    
    public boolean hasParameter(String name) {
        return parameters.stream().anyMatch(p -> p.getName().equals(name));
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String name;
        private String description;
        private SkillCategory category;
        private final List<SkillParameter> parameters = new ArrayList<>();
        private final Map<String, Object> defaultParameters = new HashMap<>();
        private Duration estimatedDuration = Duration.ofSeconds(5);
        private boolean interruptible = true;
        private final List<String> requiredSkills = new ArrayList<>();
        private final List<String> incompatibleSkills = new ArrayList<>();
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder category(SkillCategory category) {
            this.category = category;
            return this;
        }
        
        public Builder addParameter(SkillParameter parameter) {
            this.parameters.add(parameter);
            return this;
        }
        
        public Builder defaultParameter(String key, Object value) {
            this.defaultParameters.put(key, value);
            return this;
        }
        
        public Builder estimatedDuration(Duration duration) {
            this.estimatedDuration = duration;
            return this;
        }
        
        public Builder interruptible(boolean interruptible) {
            this.interruptible = interruptible;
            return this;
        }
        
        public Builder requiresSkill(String skillName) {
            this.requiredSkills.add(skillName);
            return this;
        }
        
        public Builder incompatibleWith(String skillName) {
            this.incompatibleSkills.add(skillName);
            return this;
        }
        
        public SkillDefinition build() {
            return new SkillDefinition(this);
        }
    }
}
