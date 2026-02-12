package org.lytharalab.csch.cerebellum;

public class PIDController {
    private double kp = 0.5;
    private double ki = 0.01;
    private double kd = 0.1;
    
    private double yawIntegral = 0;
    private double pitchIntegral = 0;
    private double lastYawError = 0;
    private double lastPitchError = 0;
    
    private double maxIntegral = 10.0;
    private double maxOutput = Math.PI / 2;
    
    public double computeYawRate(double yawError) {
        yawIntegral += yawError;
        yawIntegral = clamp(yawIntegral, -maxIntegral, maxIntegral);
        
        double derivative = yawError - lastYawError;
        lastYawError = yawError;
        
        double output = kp * yawError + ki * yawIntegral + kd * derivative;
        return clamp(output, -maxOutput, maxOutput);
    }
    
    public double computePitchRate(double pitchError) {
        pitchIntegral += pitchError;
        pitchIntegral = clamp(pitchIntegral, -maxIntegral, maxIntegral);
        
        double derivative = pitchError - lastPitchError;
        lastPitchError = pitchError;
        
        double output = kp * pitchError + ki * pitchIntegral + kd * derivative;
        return clamp(output, -maxOutput / 2, maxOutput / 2);
    }
    
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    
    public void reset() {
        yawIntegral = 0;
        pitchIntegral = 0;
        lastYawError = 0;
        lastPitchError = 0;
    }
    
    public void setGains(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }
    
    public void setMaxIntegral(double maxIntegral) {
        this.maxIntegral = maxIntegral;
    }
    
    public void setMaxOutput(double maxOutput) {
        this.maxOutput = maxOutput;
    }
    
    public double getKp() { return kp; }
    public double getKi() { return ki; }
    public double getKd() { return kd; }
}
