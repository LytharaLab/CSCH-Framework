package org.lytharalab.csch.core.event;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EventBus {
    
    void publish(CSCHEvent event);
    
    void subscribe(EventListener listener);
    
    void unsubscribe(EventListener listener);
    
    List<EventListener> getListeners();
    
    void clearListeners();
    
    default CompletableFuture<Void> publishAsync(CSCHEvent event) {
        return CompletableFuture.runAsync(() -> publish(event));
    }
}
