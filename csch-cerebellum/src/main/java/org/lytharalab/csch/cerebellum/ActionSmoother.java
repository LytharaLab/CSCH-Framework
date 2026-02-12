package org.lytharalab.csch.cerebellum;

import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;

import java.util.List;
import java.util.ArrayList;
import java.time.Duration;
import java.time.Instant;

public class ActionSmoother {
    private final int historySize;
    private final List<MotorAction> actionHistory;
    private final double smoothingFactor;
    
    private MotorAction lastSmoothedAction;
    
    public ActionSmoother(int historySize, double smoothingFactor) {
        this.historySize = historySize;
        this.actionHistory = new ArrayList<>();
        this.smoothingFactor = smoothingFactor;
    }
    
    public ActionSmoother() {
        this(5, 0.3);
    }
    
    public MotorAction smooth(MotorAction rawAction) {
        if (rawAction == null) {
            return lastSmoothedAction != null ? lastSmoothedAction : MotorAction.idle();
        }
        
        actionHistory.add(rawAction);
        if (actionHistory.size() > historySize) {
            actionHistory.remove(0);
        }
        
        if (actionHistory.size() < 2) {
            lastSmoothedAction = rawAction;
            return rawAction;
        }
        
        double smoothedMoveForward = computeSmoothedValue(
            rawAction.getMoveForward(), 
            a -> a.getMoveForward());
        double smoothedStrafe = computeSmoothedValue(
            rawAction.getStrafe(),
            a -> a.getStrafe());
        double smoothedYawRate = computeSmoothedValue(
            rawAction.getYawRate(),
            a -> a.getYawRate());
        double smoothedPitchRate = computeSmoothedValue(
            rawAction.getPitchRate(),
            a -> a.getPitchRate());
        
        MotorAction smoothed = MotorAction.builder()
            .moveForward(smoothedMoveForward)
            .strafe(smoothedStrafe)
            .yawRate(smoothedYawRate)
            .pitchRate(smoothedPitchRate)
            .jump(rawAction.isJump())
            .sneak(rawAction.isSneak())
            .sprint(rawAction.isSprint())
            .attack(rawAction.isAttack())
            .useItem(rawAction.isUseItem())
            .build();
        
        lastSmoothedAction = smoothed;
        return smoothed;
    }
    
    private double computeSmoothedValue(double currentValue, 
                                        java.util.function.Function<MotorAction, Double> extractor) {
        if (lastSmoothedAction == null) {
            return currentValue;
        }
        
        double previousValue = extractor.apply(lastSmoothedAction);
        return previousValue + smoothingFactor * (currentValue - previousValue);
    }
    
    public double computeJerk() {
        if (actionHistory.size() < 3) {
            return 0;
        }
        
        double totalJerk = 0;
        for (int i = 2; i < actionHistory.size(); i++) {
            MotorAction a0 = actionHistory.get(i - 2);
            MotorAction a1 = actionHistory.get(i - 1);
            MotorAction a2 = actionHistory.get(i);
            
            double jerk = computeInstantJerk(a0, a1, a2);
            totalJerk += jerk;
        }
        
        return totalJerk / (actionHistory.size() - 2);
    }
    
    private double computeInstantJerk(MotorAction a0, MotorAction a1, MotorAction a2) {
        double v0 = Math.sqrt(a0.getMoveForward() * a0.getMoveForward() + 
                             a0.getStrafe() * a0.getStrafe());
        double v1 = Math.sqrt(a1.getMoveForward() * a1.getMoveForward() + 
                             a1.getStrafe() * a1.getStrafe());
        double v2 = Math.sqrt(a2.getMoveForward() * a2.getMoveForward() + 
                             a2.getStrafe() * a2.getStrafe());
        
        double acc1 = v1 - v0;
        double acc2 = v2 - v1;
        
        return Math.abs(acc2 - acc1);
    }
    
    public void reset() {
        actionHistory.clear();
        lastSmoothedAction = null;
    }
    
    public List<MotorAction> getHistory() {
        return new ArrayList<>(actionHistory);
    }
    
    public double getSmoothness() {
        double jerk = computeJerk();
        return Math.max(0, 1.0 - jerk);
    }
}
