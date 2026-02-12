package org.lytharalab.csch.core.skill;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.HashMap;

public class SkillResult {
    private final String skillCallId;
    private final String skillName;
    private final SkillResultStatus status;
    private final String message;
    private final Duration executionTime;
    private final Instant completedAt;
    private final Map<String, Object> outputs;
    private final Map<String, Object> metrics;
    
    private SkillResult(Builder builder) {
        this.skillCallId = builder.skillCallId;
        this.skillName = builder.skillName;
        this.status = builder.status;
        this.message = builder.message;
        this.executionTime = builder.executionTime;
        this.completedAt = Instant.now();
        this.outputs = new HashMap<>(builder.outputs);
        this.metrics = new HashMap<>(builder.metrics);
    }
    
    public String getSkillCallId() { return skillCallId; }
    public String getSkillName() { return skillName; }
    public SkillResultStatus getStatus() { return status; }
    public String getMessage() { return message; }
    public Duration getExecutionTime() { return executionTime; }
    public Instant getCompletedAt() { return completedAt; }
    public Map<String, Object> getOutputs() { return outputs; }
    public Map<String, Object> getMetrics() { return metrics; }
    
    public boolean isSuccess() {
        return status == SkillResultStatus.SUCCESS;
    }
    
    public boolean isFailure() {
        return status == SkillResultStatus.FAILED || status == SkillResultStatus.INTERRUPTED;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getOutput(String key) {
        return (T) outputs.get(key);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getMetric(String key) {
        return (T) metrics.get(key);
    }
    
    public static SkillResult success(String skillCallId, String skillName, Duration executionTime) {
        return builder()
            .skillCallId(skillCallId)
            .skillName(skillName)
            .status(SkillResultStatus.SUCCESS)
            .executionTime(executionTime)
            .build();
    }
    
    public static SkillResult failure(String skillCallId, String skillName, String message) {
        return builder()
            .skillCallId(skillCallId)
            .skillName(skillName)
            .status(SkillResultStatus.FAILED)
            .message(message)
            .build();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String skillCallId;
        private String skillName;
        private SkillResultStatus status;
        private String message;
        private Duration executionTime;
        private final Map<String, Object> outputs = new HashMap<>();
        private final Map<String, Object> metrics = new HashMap<>();
        
        public Builder skillCallId(String id) {
            this.skillCallId = id;
            return this;
        }
        
        public Builder skillName(String name) {
            this.skillName = name;
            return this;
        }
        
        public Builder status(SkillResultStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder executionTime(Duration time) {
            this.executionTime = time;
            return this;
        }
        
        public Builder output(String key, Object value) {
            this.outputs.put(key, value);
            return this;
        }
        
        public Builder metric(String key, Object value) {
            this.metrics.put(key, value);
            return this;
        }
        
        public SkillResult build() {
            return new SkillResult(this);
        }
    }
}
