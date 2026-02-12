package org.lytharalab.csch.core.event;

public interface EventListener {
    
    void onEvent(CSCHEvent event);
    
    String getName();
    
    boolean isInterestedIn(String eventType);
    
    default int getPriority() {
        return 0;
    }
}
