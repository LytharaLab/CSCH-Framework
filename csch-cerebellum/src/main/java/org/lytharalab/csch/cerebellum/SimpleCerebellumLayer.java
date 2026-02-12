package org.lytharalab.csch.cerebellum;

import org.lytharalab.csch.core.action.MotorAction;
import org.lytharalab.csch.core.skill.SkillCall;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;
import org.lytharalab.csch.core.layer.ControlMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.time.Duration;

public class SimpleCerebellumLayer extends AbstractCerebellumLayer {
    
    private final ActionSpace actionSpace;
    private final ControlQualityReward rewardCalculator;
    private final ActionSmoother actionSmoother;
    private final PIDController pidController;
    
    private ControlMetrics currentMetrics;
    private SkillContext currentContext;
    private double cumulativeReward;
    private int stepCount;
    
    public SimpleCerebellumLayer() {
        this.actionSpace = ActionSpace.createDefault();
        this.rewardCalculator = new ControlQualityReward();
        this.actionSmoother = new ActionSmoother();
        this.pidController = new PIDController();
        this.currentMetrics = ControlMetrics.builder().build();
    }
    
    public SimpleCerebellumLayer(ActionSpace actionSpace) {
        this.actionSpace = actionSpace;
        this.rewardCalculator = new ControlQualityReward();
        this.actionSmoother = new ActionSmoother();
        this.pidController = new PIDController();
        this.currentMetrics = ControlMetrics.builder().build();
    }
    
    @Override
    protected void doInitialize() throws Exception {
        logger.info("Initializing SimpleCerebellumLayer");
        pidController.reset();
        actionSmoother.reset();
        cumulativeReward = 0;
        stepCount = 0;
    }
    
    @Override
    protected void doShutdown() throws Exception {
        logger.info("Shutting down SimpleCerebellumLayer");
        resetControlState();
    }
    
    @Override
    public MotorAction computeMotorAction(SkillCall skillCall, WorldState currentState) {
        if (skillCall == null || currentState == null) {
            return MotorAction.idle();
        }
        
        currentContext = new SkillContext(skillCall, currentState);
        
        MotorAction rawAction = computeRawAction(currentContext);
        
        MotorAction constrainedAction = constrainToActionSpace(rawAction);
        
        MotorAction smoothedAction = actionSmoother.smooth(constrainedAction);
        
        updateMetrics(smoothedAction, currentState);
        
        stepCount++;
        
        return smoothedAction;
    }
    
    private MotorAction computeRawAction(SkillContext context) {
        String mode = context.getMode();
        
        return switch (mode) {
            case "navigation" -> computeNavigationAction(context);
            case "alignment" -> computeAlignmentAction(context);
            case "mining" -> computeMiningAction(context);
            case "combat" -> computeCombatAction(context);
            case "escape" -> computeEscapeAction(context);
            default -> computeDefaultAction(context);
        };
    }
    
    private MotorAction computeNavigationAction(SkillContext context) {
        WorldState state = context.getWorldState();
        if (state == null || state.getPlayerState() == null) {
            return MotorAction.idle();
        }
        
        PlayerState player = state.getPlayerState();
        
        double dx = context.getTargetX() - player.getPositionX();
        double dz = context.getTargetZ() - player.getPositionZ();
        
        double targetYaw = Math.toDegrees(Math.atan2(-dx, dz));
        double yawError = normalizeAngle(targetYaw - player.getYaw());
        
        double yawRate = pidController.computeYawRate(yawError);
        
        double distance = context.computeDistanceToTarget();
        double moveForward = distance > 1.0 ? context.getSpeed() : distance * context.getSpeed();
        
        if (context.isCautious()) {
            moveForward *= 0.7;
        }
        
        return MotorAction.builder()
            .moveForward(moveForward)
            .yawRate(yawRate)
            .sprint(moveForward > 0.8 && !context.isCautious())
            .build();
    }
    
    private MotorAction computeAlignmentAction(SkillContext context) {
        WorldState state = context.getWorldState();
        if (state == null || state.getPlayerState() == null) {
            return MotorAction.idle();
        }
        
        double yawError = context.computeYawError();
        double pitchError = context.computePitchError();
        
        double yawRate = pidController.computeYawRate(yawError);
        double pitchRate = pidController.computePitchRate(pitchError);
        
        double tolerance = context.getContextData("tolerance", 0.05);
        boolean converged = Math.abs(yawError) < tolerance * 180 && 
                           Math.abs(pitchError) < tolerance * 180;
        
        setConverged(converged);
        
        return MotorAction.builder()
            .yawRate(yawRate)
            .pitchRate(pitchRate)
            .build();
    }
    
    private MotorAction computeMiningAction(SkillContext context) {
        MotorAction alignAction = computeAlignmentAction(context);
        
        return MotorAction.builder()
            .moveForward(alignAction.getMoveForward())
            .strafe(alignAction.getStrafe())
            .yawRate(alignAction.getYawRate())
            .pitchRate(alignAction.getPitchRate())
            .attack(true)
            .build();
    }
    
    private MotorAction computeCombatAction(SkillContext context) {
        WorldState state = context.getWorldState();
        if (state == null || state.getPlayerState() == null) {
            return MotorAction.idle();
        }
        
        MotorAction alignAction = computeAlignmentAction(context);
        
        double distance = context.getContextData("distance", 3.5);
        double currentDistance = context.computeDistanceToTarget();
        
        double strafe = 0;
        if (currentDistance < distance - 0.5) {
            strafe = -0.5;
        } else if (currentDistance > distance + 0.5) {
            strafe = 0.5;
        }
        
        return MotorAction.builder()
            .moveForward(alignAction.getMoveForward())
            .strafe(strafe)
            .yawRate(alignAction.getYawRate())
            .pitchRate(alignAction.getPitchRate())
            .attack(true)
            .build();
    }
    
    private MotorAction computeEscapeAction(SkillContext context) {
        WorldState state = context.getWorldState();
        if (state == null || state.getPlayerState() == null) {
            return MotorAction.idle();
        }
        
        PlayerState player = state.getPlayerState();
        
        double yawRate = -Math.signum(player.getYaw()) * actionSpace.getYawRateMax() * 0.5;
        
        return MotorAction.builder()
            .moveForward(context.getSpeed())
            .yawRate(yawRate)
            .sprint(true)
            .build();
    }
    
    private MotorAction computeDefaultAction(SkillContext context) {
        return MotorAction.idle();
    }
    
    private MotorAction constrainToActionSpace(MotorAction action) {
        return MotorAction.builder()
            .moveForward(actionSpace.clampMoveForward(action.getMoveForward()))
            .strafe(actionSpace.clampStrafe(action.getStrafe()))
            .yawRate(actionSpace.clampYawRate(action.getYawRate()))
            .pitchRate(actionSpace.clampPitchRate(action.getPitchRate()))
            .jump(actionSpace.isJumpAllowed() && action.isJump())
            .sneak(actionSpace.isSneakAllowed() && action.isSneak())
            .sprint(actionSpace.isSprintAllowed() && action.isSprint())
            .attack(action.isAttack())
            .useItem(action.isUseItem())
            .build();
    }
    
    private void updateMetrics(MotorAction action, WorldState state) {
        double aimError = 0;
        if (currentContext != null) {
            aimError = Math.sqrt(
                Math.pow(currentContext.computeYawError(), 2) +
                Math.pow(currentContext.computePitchError(), 2)
            ) / 180.0;
        }
        
        currentMetrics = ControlMetrics.builder()
            .aimError(aimError)
            .jerk(actionSmoother.computeJerk())
            .smoothness(actionSmoother.getSmoothness())
            .stability(1.0 - aimError)
            .build();
    }
    
    @Override
    public List<MotorAction> computeActionSequence(SkillCall skillCall, WorldState currentState, int horizon) {
        List<MotorAction> sequence = new ArrayList<>();
        
        for (int i = 0; i < horizon; i++) {
            MotorAction action = computeMotorAction(skillCall, currentState);
            sequence.add(action);
        }
        
        return sequence;
    }
    
    @Override
    public void updateFromFeedback(WorldState previousState, MotorAction action, 
                                   WorldState currentState, double reward) {
        cumulativeReward += reward;
        
        rewardCalculator.computeReward(previousState, currentState, 
            currentMetrics.getAimError(), false, false);
        
        if (stepCount % 100 == 0) {
            logger.debug("Cumulative reward after {} steps: {}", stepCount, cumulativeReward);
        }
    }
    
    @Override
    public ControlMetrics getControlMetrics() {
        return currentMetrics;
    }
    
    @Override
    public void resetControlState() {
        pidController.reset();
        actionSmoother.reset();
        rewardCalculator.reset();
        currentContext = null;
        currentMetrics = ControlMetrics.builder().build();
        cumulativeReward = 0;
        stepCount = 0;
        setConverged(false);
    }
    
    private double normalizeAngle(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
}
