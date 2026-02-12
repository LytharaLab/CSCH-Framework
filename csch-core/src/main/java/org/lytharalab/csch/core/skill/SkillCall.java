package org.lytharalab.csch.core.skill;

import java.time.Instant;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.UUID;

public class SkillCall {
    private final String id;
    private final String skillName;
    private final Map<String, Object> parameters;
    private final SkillPriority priority;
    private final Instant timestamp;
    private final String intentId;
    
    private SkillCall(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.skillName = builder.skillName;
        this.parameters = Collections.unmodifiableMap(new HashMap<>(builder.parameters));
        this.priority = builder.priority != null ? builder.priority : SkillPriority.NORMAL;
        this.timestamp = Instant.now();
        this.intentId = builder.intentId;
    }
    
    public String getId() { return id; }
    public String getSkillName() { return skillName; }
    public Map<String, Object> getParameters() { return parameters; }
    public SkillPriority getPriority() { return priority; }
    public Instant getTimestamp() { return timestamp; }
    public String getIntentId() { return intentId; }
    
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key) {
        return (T) parameters.get(key);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key, T defaultValue) {
        Object value = parameters.get(key);
        return value != null ? (T) value : defaultValue;
    }
    
    public boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String id;
        private String skillName;
        private final Map<String, Object> parameters = new HashMap<>();
        private SkillPriority priority;
        private String intentId;
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder skillName(String name) {
            this.skillName = name;
            return this;
        }
        
        public Builder parameter(String key, Object value) {
            this.parameters.put(key, value);
            return this;
        }
        
        public Builder parameters(Map<String, Object> params) {
            this.parameters.putAll(params);
            return this;
        }
        
        public Builder priority(SkillPriority priority) {
            this.priority = priority;
            return this;
        }
        
        public Builder intentId(String intentId) {
            this.intentId = intentId;
            return this;
        }
        
        public SkillCall build() {
            return new SkillCall(this);
        }
    }
}
