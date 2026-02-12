package org.lytharalab.csch.core;

public final class CSCHConstants {
    private CSCHConstants() {}
    
    public static final String FRAMEWORK_NAME = "CSCH Framework";
    public static final String FRAMEWORK_VERSION = "1.0.0";
    
    public static final int DEFAULT_CONTROL_FREQUENCY_HZ = 60;
    public static final int DEFAULT_INTENT_UPDATE_INTERVAL_MS = 1000;
    public static final int DEFAULT_SKILL_UPDATE_INTERVAL_MS = 100;
    public static final int DEFAULT_MOTOR_UPDATE_INTERVAL_MS = 16;
    
    public static final double DEFAULT_ACTION_SMOOTHNESS_THRESHOLD = 0.1;
    public static final double DEFAULT_COLLISION_THRESHOLD = 0.5;
    public static final double DEFAULT_AIM_ERROR_THRESHOLD = 0.05;
    
    public static final int MAX_ACTION_HISTORY_SIZE = 1000;
    public static final int MAX_SKILL_HISTORY_SIZE = 100;
    public static final int MAX_INTENT_HISTORY_SIZE = 50;
    
    public static final double SAFETY_SHIELD_DEFAULT_THRESHOLD = 0.8;
    public static final double EMERGENCY_STOP_THRESHOLD = 0.95;
}
