package org.lytharalab.csch.safety;

import org.lytharalab.csch.core.common.CSCHLayer;
import org.lytharalab.csch.core.layer.CSCHComponent;
import org.lytharalab.csch.core.layer.CSCHException;
import org.lytharalab.csch.core.layer.SafetyShield;
import org.lytharalab.csch.core.layer.SafetyRule;
import org.lytharalab.csch.core.layer.SafetyViolation;
import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.action.SafeMotorAction;
import org.lytharalab.csch.core.state.WorldState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractSafetyShield implements SafetyShield {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final AtomicBoolean initialized = new AtomicBoolean(false);
    protected final AtomicBoolean healthy = new AtomicBoolean(true);
    protected final AtomicInteger violationCount = new AtomicInteger(0);
    protected final Map<String, SafetyRule> rules = new ConcurrentHashMap<>();
    
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
    
    @Override
    public CSCHLayer getLayer() {
        return CSCHLayer.SAFETY;
    }
    
    @Override
    public void initialize() throws CSCHException {
        if (initialized.compareAndSet(false, true)) {
            try {
                doInitialize();
                logger.info("Safety shield initialized: {}", getName());
            } catch (Exception e) {
                initialized.set(false);
                healthy.set(false);
                throw new CSCHException(getName(), CSCHException.ErrorCode.INITIALIZATION_FAILED,
                    "Failed to initialize safety shield", e);
            }
        }
    }
    
    @Override
    public void shutdown() throws CSCHException {
        if (initialized.compareAndSet(true, false)) {
            try {
                doShutdown();
                logger.info("Safety shield shutdown: {}", getName());
            } catch (Exception e) {
                throw new CSCHException(getName(), CSCHException.ErrorCode.SHUTDOWN_FAILED,
                    "Failed to shutdown safety shield", e);
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
    public void addRule(SafetyRule rule) {
        if (rule != null && rule.getId() != null) {
            rules.put(rule.getId(), rule);
            logger.debug("Added safety rule: {}", rule.getName());
        }
    }
    
    @Override
    public void removeRule(String ruleId) {
        SafetyRule removed = rules.remove(ruleId);
        if (removed != null) {
            logger.debug("Removed safety rule: {}", removed.getName());
        }
    }
    
    @Override
    public List<SafetyRule> getActiveRules() {
        return new ArrayList<>(rules.values());
    }
    
    @Override
    public int getViolationCount() {
        return violationCount.get();
    }
    
    @Override
    public void resetViolationCount() {
        violationCount.set(0);
    }
    
    protected void incrementViolationCount() {
        violationCount.incrementAndGet();
    }
    
    protected abstract void doInitialize() throws Exception;
    
    protected abstract void doShutdown() throws Exception;
}
