package org.lytharalab.csch.api;

import org.lytharalab.csch.core.config.CSCHConfiguration;
import org.lytharalab.csch.core.event.EventBus;
import org.lytharalab.csch.core.state.StateProvider;

public final class CSCHFactory {
    
    private CSCHFactory() {}
    
    public static CSCHSystem createSystem(StateProvider stateProvider) {
        return new CSCHSystem(stateProvider);
    }
    
    public static CSCHSystem createSystem(StateProvider stateProvider, CSCHConfiguration configuration) {
        return new CSCHSystem(configuration, stateProvider, new DefaultEventBus());
    }
    
    public static CSCHSystem createSystem(StateProvider stateProvider, CSCHConfiguration configuration, EventBus eventBus) {
        return new CSCHSystem(configuration, stateProvider, eventBus);
    }
    
    public static CSCHAgent createAgent(StateProvider stateProvider) {
        return new CSCHAgent(stateProvider);
    }
    
    public static CSCHAgent createAgent(StateProvider stateProvider, CSCHConfiguration configuration) {
        return new CSCHAgent(stateProvider, configuration, new DefaultEventBus());
    }
    
    public static CSCHAgent createAgent(StateProvider stateProvider, CSCHConfiguration configuration, EventBus eventBus) {
        return new CSCHAgent(stateProvider, configuration, eventBus);
    }
    
    public static CSCHConfiguration.Builder configurationBuilder() {
        return CSCHConfiguration.builder();
    }
    
    public static CSCHConfiguration defaultConfiguration() {
        return CSCHConfiguration.defaultConfiguration();
    }
    
    public static MockStateProvider mockStateProvider() {
        return new MockStateProvider();
    }
    
    public static EventBus defaultEventBus() {
        return new DefaultEventBus();
    }
}
