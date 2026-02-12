package org.lytharalab.csch.core.intent;

import java.time.Instant;
import java.util.UUID;

public class IntentNode {
    private final String id;
    private final Intent intent;
    private final IntentNodeStatus status;
    private final Instant createdAt;
    private final Instant startedAt;
    private final Instant completedAt;
    
    private IntentNode(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.intent = builder.intent;
        this.status = builder.status != null ? builder.status : IntentNodeStatus.PENDING;
        this.createdAt = Instant.now();
        this.startedAt = builder.startedAt;
        this.completedAt = builder.completedAt;
    }
    
    public String getId() { return id; }
    public Intent getIntent() { return intent; }
    public IntentNodeStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    
    public boolean isCompleted() {
        return status == IntentNodeStatus.COMPLETED || status == IntentNodeStatus.SKIPPED;
    }
    
    public boolean isFailed() {
        return status == IntentNodeStatus.FAILED;
    }
    
    public boolean isActive() {
        return status == IntentNodeStatus.RUNNING;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String id;
        private Intent intent;
        private IntentNodeStatus status;
        private Instant startedAt;
        private Instant completedAt;
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder intent(Intent intent) {
            this.intent = intent;
            return this;
        }
        
        public Builder status(IntentNodeStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder startedAt(Instant startedAt) {
            this.startedAt = startedAt;
            return this;
        }
        
        public Builder completedAt(Instant completedAt) {
            this.completedAt = completedAt;
            return this;
        }
        
        public IntentNode build() {
            return new IntentNode(this);
        }
    }
}
