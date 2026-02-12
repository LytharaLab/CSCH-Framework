package org.lytharalab.csch.core.intent;

import org.lytharalab.csch.core.common.Priority;

import java.time.Instant;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class Intent {
    private final String id;
    private final String description;
    private final IntentType type;
    private final Priority priority;
    private final Map<String, Object> parameters;
    private final Instant timestamp;
    private final String parentId;
    
    private Intent(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.description = builder.description;
        this.type = builder.type;
        this.priority = builder.priority != null ? builder.priority : Priority.NORMAL;
        this.parameters = Collections.unmodifiableMap(new HashMap<>(builder.parameters));
        this.timestamp = Instant.now();
        this.parentId = builder.parentId;
    }
    
    public String getId() { return id; }
    public String getDescription() { return description; }
    public IntentType getType() { return type; }
    public Priority getPriority() { return priority; }
    public Map<String, Object> getParameters() { return parameters; }
    public Instant getTimestamp() { return timestamp; }
    public String getParentId() { return parentId; }
    
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
        private String description;
        private IntentType type = IntentType.GENERIC;
        private Priority priority;
        private final Map<String, Object> parameters = new HashMap<>();
        private String parentId;
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder type(IntentType type) {
            this.type = type;
            return this;
        }
        
        public Builder priority(Priority priority) {
            this.priority = priority;
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
        
        public Builder parentId(String parentId) {
            this.parentId = parentId;
            return this;
        }
        
        public Intent build() {
            return new Intent(this);
        }
    }
}
