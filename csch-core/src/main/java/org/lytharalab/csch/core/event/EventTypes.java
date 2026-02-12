package org.lytharalab.csch.core.event;

public final class EventTypes {
    private EventTypes() {}
    
    public static final String INTENT_GENERATED = "intent.generated";
    public static final String INTENT_UPDATED = "intent.updated";
    public static final String INTENT_COMPLETED = "intent.completed";
    public static final String INTENT_FAILED = "intent.failed";
    
    public static final String SKILL_CALLED = "skill.called";
    public static final String SKILL_STARTED = "skill.started";
    public static final String SKILL_COMPLETED = "skill.completed";
    public static final String SKILL_FAILED = "skill.failed";
    public static final String SKILL_INTERRUPTED = "skill.interrupted";
    
    public static final String ACTION_COMPUTED = "action.computed";
    public static final String ACTION_EXECUTED = "action.executed";
    public static final String ACTION_FILTERED = "action.filtered";
    
    public static final String SAFETY_VIOLATION = "safety.violation";
    public static final String SAFETY_INTERVENTION = "safety.intervention";
    public static final String EMERGENCY_STOP = "safety.emergency_stop";
    
    public static final String STATE_UPDATED = "state.updated";
    public static final String PLAYER_DAMAGED = "player.damaged";
    public static final String PLAYER_HEALED = "player.healed";
    public static final String PLAYER_DIED = "player.died";
    
    public static final String SYSTEM_INITIALIZED = "system.initialized";
    public static final String SYSTEM_SHUTDOWN = "system.shutdown";
    public static final String SYSTEM_ERROR = "system.error";
    
    public static final String LEARNING_UPDATE = "learning.update";
    public static final String SKILL_LEARNED = "learning.skill_learned";
    public static final String INTERVENTION_REQUIRED = "learning.intervention_required";
}
