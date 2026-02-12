package org.lytharalab.csch.core.common;

public enum ExecutionStatus {
    PENDING("待执行"),
    RUNNING("执行中"),
    SUCCESS("成功"),
    FAILED("失败"),
    INTERRUPTED("被中断"),
    TIMEOUT("超时"),
    BLOCKED("被阻塞"),
    RECOVERING("恢复中");
    
    private final String description;
    
    ExecutionStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isTerminal() {
        return this == SUCCESS || this == FAILED || this == INTERRUPTED || this == TIMEOUT;
    }
    
    public boolean isSuccess() {
        return this == SUCCESS;
    }
    
    public boolean isFailure() {
        return this == FAILED || this == INTERRUPTED || this == TIMEOUT || this == BLOCKED;
    }
}
