package org.lytharalab.csch.core.action;

import java.time.Instant;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class SafeMotorAction {
    private final MotorAction originalAction;
    private final MotorAction safeAction;
    private final boolean wasModified;
    private final String modificationReason;
    private final Instant timestamp;
    private final Map<String, Object> safetyMetadata;
    
    private SafeMotorAction(Builder builder) {
        this.originalAction = builder.originalAction;
        this.safeAction = builder.safeAction != null ? builder.safeAction : builder.originalAction;
        this.wasModified = builder.wasModified;
        this.modificationReason = builder.modificationReason;
        this.timestamp = Instant.now();
        this.safetyMetadata = Collections.unmodifiableMap(new HashMap<>(builder.safetyMetadata));
    }
    
    public MotorAction getOriginalAction() { return originalAction; }
    public MotorAction getSafeAction() { return safeAction; }
    public boolean wasModified() { return wasModified; }
    public String getModificationReason() { return modificationReason; }
    public Instant getTimestamp() { return timestamp; }
    public Map<String, Object> getSafetyMetadata() { return safetyMetadata; }
    
    public static SafeMotorAction unchanged(MotorAction action) {
        return builder().originalAction(action).safeAction(action).build();
    }
    
    public static SafeMotorAction modified(MotorAction original, MotorAction safe, String reason) {
        return builder()
            .originalAction(original)
            .safeAction(safe)
            .wasModified(true)
            .modificationReason(reason)
            .build();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private MotorAction originalAction;
        private MotorAction safeAction;
        private boolean wasModified = false;
        private String modificationReason;
        private final Map<String, Object> safetyMetadata = new HashMap<>();
        
        public Builder originalAction(MotorAction action) {
            this.originalAction = action;
            return this;
        }
        
        public Builder safeAction(MotorAction action) {
            this.safeAction = action;
            return this;
        }
        
        public Builder wasModified(boolean modified) {
            this.wasModified = modified;
            return this;
        }
        
        public Builder modificationReason(String reason) {
            this.modificationReason = reason;
            return this;
        }
        
        public Builder safetyMetadata(String key, Object value) {
            this.safetyMetadata.put(key, value);
            return this;
        }
        
        public SafeMotorAction build() {
            return new SafeMotorAction(this);
        }
    }
}
