package org.lytharalab.csch.core.state;

import java.util.function.Supplier;

public interface StateProvider extends Supplier<WorldState> {
    
    WorldState getCurrentState();
    
    PlayerState getPlayerState();
    
    EnvironmentState getEnvironmentState();
    
    @Override
    default WorldState get() {
        return getCurrentState();
    }
}
