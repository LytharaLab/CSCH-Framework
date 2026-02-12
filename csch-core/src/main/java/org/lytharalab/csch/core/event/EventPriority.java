package org.lytharalab.csch.core.event;

public enum EventPriority {
    LOW(1),
    NORMAL(5),
    HIGH(10),
    CRITICAL(20);
    
    private final int value;
    
    EventPriority(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}
