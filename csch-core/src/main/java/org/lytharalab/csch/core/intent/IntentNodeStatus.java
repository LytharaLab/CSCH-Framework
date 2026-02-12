package org.lytharalab.csch.core.intent;

public enum IntentNodeStatus {
    PENDING("待执行"),
    RUNNING("执行中"),
    COMPLETED("已完成"),
    FAILED("失败"),
    SKIPPED("已跳过"),
    BLOCKED("被阻塞");
    
    private final String description;
    
    IntentNodeStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == SKIPPED;
    }
}
