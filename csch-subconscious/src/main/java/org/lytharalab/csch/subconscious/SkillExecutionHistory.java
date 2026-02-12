package org.lytharalab.csch.subconscious;

import org.lytharalab.csch.core.skill.SkillCall;
import org.lytharalab.csch.core.skill.SkillResult;
import org.lytharalab.csch.core.skill.SkillResultStatus;
import org.lytharalab.csch.core.state.WorldState;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.Duration;
import java.time.Instant;

public class SkillExecutionHistory {
    private final int maxSize;
    private final List<ExecutionRecord> history;
    private final Map<String, ExecutionStats> skillStats;
    
    public SkillExecutionHistory(int maxSize) {
        this.maxSize = maxSize;
        this.history = new ArrayList<>();
        this.skillStats = new HashMap<>();
    }
    
    public SkillExecutionHistory() {
        this(100);
    }
    
    public synchronized void record(SkillCall call, SkillResult result, 
                                   WorldState beforeState, WorldState afterState) {
        ExecutionRecord record = new ExecutionRecord(call, result, beforeState, afterState);
        
        if (history.size() >= maxSize) {
            history.remove(0);
        }
        history.add(record);
        
        updateStats(call.getSkillName(), result);
    }
    
    private void updateStats(String skillName, SkillResult result) {
        ExecutionStats stats = skillStats.computeIfAbsent(skillName, k -> new ExecutionStats());
        stats.totalExecutions++;
        
        if (result.isSuccess()) {
            stats.successCount++;
        } else {
            stats.failureCount++;
        }
        
        if (result.getExecutionTime() != null) {
            stats.totalExecutionTimeMs += result.getExecutionTime().toMillis();
        }
        
        stats.lastExecutionTime = Instant.now();
    }
    
    public synchronized List<ExecutionRecord> getHistory() {
        return new ArrayList<>(history);
    }
    
    public synchronized List<ExecutionRecord> getHistoryForSkill(String skillName) {
        return history.stream()
            .filter(r -> r.getCall().getSkillName().equals(skillName))
            .toList();
    }
    
    public synchronized ExecutionStats getStats(String skillName) {
        return skillStats.get(skillName);
    }
    
    public synchronized Map<String, ExecutionStats> getAllStats() {
        return new HashMap<>(skillStats);
    }
    
    public synchronized int getSuccessRate(String skillName) {
        ExecutionStats stats = skillStats.get(skillName);
        if (stats == null || stats.totalExecutions == 0) {
            return 0;
        }
        return (int) (stats.successCount * 100.0 / stats.totalExecutions);
    }
    
    public synchronized void clear() {
        history.clear();
        skillStats.clear();
    }
    
    public static class ExecutionRecord {
        private final SkillCall call;
        private final SkillResult result;
        private final WorldState beforeState;
        private final WorldState afterState;
        private final Instant recordedAt;
        
        public ExecutionRecord(SkillCall call, SkillResult result, 
                              WorldState beforeState, WorldState afterState) {
            this.call = call;
            this.result = result;
            this.beforeState = beforeState;
            this.afterState = afterState;
            this.recordedAt = Instant.now();
        }
        
        public SkillCall getCall() { return call; }
        public SkillResult getResult() { return result; }
        public WorldState getBeforeState() { return beforeState; }
        public WorldState getAfterState() { return afterState; }
        public Instant getRecordedAt() { return recordedAt; }
        
        public Duration getDuration() {
            return result.getExecutionTime();
        }
        
        public boolean isSuccess() {
            return result.isSuccess();
        }
    }
    
    public static class ExecutionStats {
        private int totalExecutions = 0;
        private int successCount = 0;
        private int failureCount = 0;
        private long totalExecutionTimeMs = 0;
        private Instant lastExecutionTime;
        
        public int getTotalExecutions() { return totalExecutions; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public long getTotalExecutionTimeMs() { return totalExecutionTimeMs; }
        public Instant getLastExecutionTime() { return lastExecutionTime; }
        
        public double getSuccessRate() {
            return totalExecutions > 0 ? (double) successCount / totalExecutions : 0;
        }
        
        public double getAverageExecutionTimeMs() {
            return totalExecutions > 0 ? (double) totalExecutionTimeMs / totalExecutions : 0;
        }
    }
}
