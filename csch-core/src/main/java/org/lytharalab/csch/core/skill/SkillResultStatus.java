package org.lytharalab.csch.core.skill;

public enum SkillResultStatus {
    SUCCESS("成功"),
    FAILED("失败"),
    INTERRUPTED("被中断"),
    TIMEOUT("超时"),
    BLOCKED("被阻塞"),
    SKIPPED("已跳过");
    
    private final String description;
    
    SkillResultStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isTerminal() {
        return true;
    }
    
    public boolean isSuccess() {
        return this == SUCCESS || this == SKIPPED;
    }
}
