package org.lytharalab.csch.core.event;

import java.time.Instant;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.UUID;

public class CSCHEvent {
    private final String id;
    private final String type;
    private final String source;
    private final Instant timestamp;
    private final Map<String, Object> data;
    private final EventPriority priority;
    
    private CSCHEvent(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.type = builder.type;
        this.source = builder.source;
        this.timestamp = Instant.now();
        this.data = Collections.unmodifiableMap(new HashMap<>(builder.data));
        this.priority = builder.priority != null ? builder.priority : EventPriority.NORMAL;
    }
    
    public String getId() { return id; }
    public String getType() { return type; }
    public String getSource() { return source; }
    public Instant getTimestamp() { return timestamp; }
    public Map<String, Object> getData() { return data; }
    public EventPriority getPriority() { return priority; }
    
    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) data.get(key);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getData(String key, T defaultValue) {
        Object value = data.get(key);
        return value != null ? (T) value : defaultValue;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String id;
        private String type;
        private String source;
        private final Map<String, Object> data = new HashMap<>();
        private EventPriority priority;
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder type(String type) {
            this.type = type;
            return this;
        }
        
        public Builder source(String source) {
            this.source = source;
            return this;
        }
        
        public Builder data(String key, Object value) {
            this.data.put(key, value);
            return this;
        }
        
        public Builder priority(EventPriority priority) {
            this.priority = priority;
            return this;
        }
        
        public CSCHEvent build() {
            return new CSCHEvent(this);
        }
    }
}
