package org.lytharalab.csch.cerebellum;

import org.lytharalab.csch.core.common.CSCHLayer;
import org.lytharalab.csch.core.layer.CSCHComponent;
import org.lytharalab.csch.core.layer.CSCHException;
import org.lytharalab.csch.core.layer.CerebellumLayer;
import org.lytharalab.csch.core.layer.ControlMetrics;
import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.skill.SkillCall;
import org.lytharalab.csch.core.state.WorldState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractCerebellumLayer implements CerebellumLayer {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final AtomicBoolean initialized = new AtomicBoolean(false);
    protected final AtomicBoolean healthy = new AtomicBoolean(true);
    protected final AtomicBoolean converged = new AtomicBoolean(false);
    
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
    
    @Override
    public CSCHLayer getLayer() {
        return CSCHLayer.CEREBELLUM;
    }
    
    @Override
    public void initialize() throws CSCHException {
        if (initialized.compareAndSet(false, true)) {
            try {
                doInitialize();
                logger.info("Cerebellum layer initialized: {}", getName());
            } catch (Exception e) {
                initialized.set(false);
                healthy.set(false);
                throw new CSCHException(getName(), CSCHException.ErrorCode.INITIALIZATION_FAILED,
                    "Failed to initialize cerebellum layer", e);
            }
        }
    }
    
    @Override
    public void shutdown() throws CSCHException {
        if (initialized.compareAndSet(true, false)) {
            try {
                doShutdown();
                logger.info("Cerebellum layer shutdown: {}", getName());
            } catch (Exception e) {
                throw new CSCHException(getName(), CSCHException.ErrorCode.SHUTDOWN_FAILED,
                    "Failed to shutdown cerebellum layer", e);
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
    public boolean isConverged() {
        return converged.get();
    }
    
    protected void setConverged(boolean value) {
        converged.set(value);
    }
    
    protected abstract void doInitialize() throws Exception;
    
    protected abstract void doShutdown() throws Exception;
}
