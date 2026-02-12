package org.lytharalab.csch.api;

import org.lytharalab.csch.core.event.CSCHEvent;
import org.lytharalab.csch.core.event.EventBus;
import org.lytharalab.csch.core.event.EventListener;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultEventBus implements EventBus {
    
    private final List<EventListener> listeners = new CopyOnWriteArrayList<>();
    
    @Override
    public void publish(CSCHEvent event) {
        for (EventListener listener : listeners) {
            try {
                if (listener.isInterestedIn(event.getType())) {
                    listener.onEvent(event);
                }
            } catch (Exception e) {
                System.err.println("Error dispatching event to listener " + listener.getName() + ": " + e.getMessage());
            }
        }
    }
    
    @Override
    public void subscribe(EventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    @Override
    public void unsubscribe(EventListener listener) {
        listeners.remove(listener);
    }
    
    @Override
    public List<EventListener> getListeners() {
        return new ArrayList<>(listeners);
    }
    
    @Override
    public void clearListeners() {
        listeners.clear();
    }
}
