package org.lytharalab.csch.core.skill;

public enum SkillPriority {
    LOW(1),
    NORMAL(5),
    HIGH(10),
    CRITICAL(20),
    INTERRUPT(100);
    
    private final int value;
    
    SkillPriority(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public boolean isHigherThan(SkillPriority other) {
        return this.value > other.value;
    }
    
    public boolean isInterrupt() {
        return this == INTERRUPT;
    }
}
