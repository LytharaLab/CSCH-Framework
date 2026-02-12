package org.lytharalab.csch.core.layer;

import java.time.Instant;

public class SafetyViolation {
    private final String ruleId;
    private final String ruleName;
    private final ViolationSeverity severity;
    private final String description;
    private final Instant timestamp;
    private final String suggestedAction;
    
    public SafetyViolation(String ruleId, String ruleName, ViolationSeverity severity, 
                          String description, String suggestedAction) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.severity = severity;
        this.description = description;
        this.timestamp = Instant.now();
        this.suggestedAction = suggestedAction;
    }
    
    public String getRuleId() { return ruleId; }
    public String getRuleName() { return ruleName; }
    public ViolationSeverity getSeverity() { return severity; }
    public String getDescription() { return description; }
    public Instant getTimestamp() { return timestamp; }
    public String getSuggestedAction() { return suggestedAction; }
    
    public boolean isCritical() {
        return severity == ViolationSeverity.CRITICAL || severity == ViolationSeverity.EMERGENCY;
    }
    
    public enum ViolationSeverity {
        INFO(1),
        WARNING(5),
        ERROR(10),
        CRITICAL(20),
        EMERGENCY(100);
        
        private final int level;
        
        ViolationSeverity(int level) {
            this.level = level;
        }
        
        public int getLevel() {
            return level;
        }
        
        public boolean isHigherThan(ViolationSeverity other) {
            return this.level > other.level;
        }
    }
}
