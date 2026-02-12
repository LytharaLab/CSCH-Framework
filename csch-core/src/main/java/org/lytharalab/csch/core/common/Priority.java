package org.lytharalab.csch.core.common;

public enum Priority {
    LOW(1),
    NORMAL(5),
    HIGH(10),
    CRITICAL(20),
    EMERGENCY(100);
    
    private final int value;
    
    Priority(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public boolean isHigherThan(Priority other) {
        return this.value > other.value;
    }
    
    public boolean isLowerThan(Priority other) {
        return this.value < other.value;
    }
}
