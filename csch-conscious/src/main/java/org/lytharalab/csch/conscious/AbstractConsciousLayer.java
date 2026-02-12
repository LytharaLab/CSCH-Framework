package org.lytharalab.csch.conscious;

import org.lytharalab.csch.core.common.CSCHLayer;
import org.lytharalab.csch.core.layer.CSCHComponent;
import org.lytharalab.csch.core.layer.CSCHException;
import org.lytharalab.csch.core.layer.ConsciousLayer;
import org.lytharalab.csch.core.intent.Intent;
import org.lytharalab.csch.core.intent.IntentGraph;
import org.lytharalab.csch.core.intent.IntentNode;
import org.lytharalab.csch.core.intent.IntentType;
import org.lytharalab.csch.core.state.WorldState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractConsciousLayer implements ConsciousLayer {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final AtomicBoolean initialized = new AtomicBoolean(false);
    protected final AtomicBoolean healthy = new AtomicBoolean(true);
    
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
    
    @Override
    public CSCHLayer getLayer() {
        return CSCHLayer.CONSCIOUS;
    }
    
    @Override
    public void initialize() throws CSCHException {
        if (initialized.compareAndSet(false, true)) {
            try {
                doInitialize();
                logger.info("Conscious layer initialized: {}", getName());
            } catch (Exception e) {
                initialized.set(false);
                healthy.set(false);
                throw new CSCHException(getName(), CSCHException.ErrorCode.INITIALIZATION_FAILED, 
                    "Failed to initialize conscious layer", e);
            }
        }
    }
    
    @Override
    public void shutdown() throws CSCHException {
        if (initialized.compareAndSet(true, false)) {
            try {
                doShutdown();
                logger.info("Conscious layer shutdown: {}", getName());
            } catch (Exception e) {
                throw new CSCHException(getName(), CSCHException.ErrorCode.SHUTDOWN_FAILED,
                    "Failed to shutdown conscious layer", e);
            }
        }
    }
    
    @Override
    public boolean isInitialized() {
        return initialized.get();
    }
    
    @Override
    public boolean isHealthy() {
        return healthy.get() && initialized.get();
    }
    
    protected abstract void doInitialize() throws Exception;
    
    protected abstract void doShutdown() throws Exception;
}
