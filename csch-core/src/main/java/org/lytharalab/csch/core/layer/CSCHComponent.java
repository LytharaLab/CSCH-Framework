package org.lytharalab.csch.core.layer;

import org.lytharalab.csch.core.common.CSCHLayer;
import org.lytharalab.csch.core.state.WorldState;

import java.util.concurrent.CompletableFuture;

public interface CSCHComponent {
    
    String getName();
    
    CSCHLayer getLayer();
    
    void initialize() throws CSCHException;
    
    void shutdown() throws CSCHException;
    
    boolean isInitialized();
    
    boolean isHealthy();
    
    default CompletableFuture<Void> initializeAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                initialize();
            } catch (CSCHException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    default CompletableFuture<Void> shutdownAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                shutdown();
            } catch (CSCHException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
