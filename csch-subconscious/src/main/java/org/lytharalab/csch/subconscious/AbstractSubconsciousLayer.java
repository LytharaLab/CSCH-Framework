package org.lytharalab.csch.subconscious;

import org.lytharalab.csch.core.common.CSCHLayer;
import org.lytharalab.csch.core.layer.CSCHComponent;
import org.lytharalab.csch.core.layer.CSCHException;
import org.lytharalab.csch.core.layer.SubconsciousLayer;
import org.lytharalab.csch.core.intent.Intent;
import org.lytharalab.csch.core.skill.*;
import org.lytharalab.csch.core.state.WorldState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractSubconsciousLayer implements SubconsciousLayer {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final AtomicBoolean initialized = new AtomicBoolean(false);
    protected final AtomicBoolean healthy = new AtomicBoolean(true);
    protected final AtomicInteger interventionCount = new AtomicInteger(0);
    
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
    
    @Override
    public CSCHLayer getLayer() {
        return CSCHLayer.SUBCONSCIOUS;
    }
    
    @Override
    public void initialize() throws CSCHException {
        if (initialized.compareAndSet(false, true)) {
            try {
                doInitialize();
                logger.info("Subconscious layer initialized: {}", getName());
            } catch (Exception e) {
                initialized.set(false);
                healthy.set(false);
                throw new CSCHException(getName(), CSCHException.ErrorCode.INITIALIZATION_FAILED,
                    "Failed to initialize subconscious layer", e);
            }
        }
    }
    
    @Override
    public void shutdown() throws CSCHException {
        if (initialized.compareAndSet(true, false)) {
            try {
                doShutdown();
                logger.info("Subconscious layer shutdown: {}", getName());
            } catch (Exception e) {
                throw new CSCHException(getName(), CSCHException.ErrorCode.SHUTDOWN_FAILED,
                    "Failed to shutdown subconscious layer", e);
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
    
    @Override
    public int getInterventionCount() {
        return interventionCount.get();
    }
    
    @Override
    public void resetInterventionCount() {
        interventionCount.set(0);
    }
    
    protected void incrementInterventionCount() {
        interventionCount.incrementAndGet();
    }
    
    protected abstract void doInitialize() throws Exception;
    
    protected abstract void doShutdown() throws Exception;
}
