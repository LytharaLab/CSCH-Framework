package org.lytharalab.csch.conscious;

import org.lytharalab.csch.core.intent.IntentGraph;
import org.lytharalab.csch.core.intent.IntentNode;
import org.lytharalab.csch.core.intent.IntentNodeStatus;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.Duration;
import java.time.Instant;

public class ReflectionGenerator {
    
    public String generateReflection(WorldState currentState, IntentGraph executedGraph) {
        StringBuilder reflection = new StringBuilder();
        
        reflection.append("=== 执行反思报告 ===\n\n");
        
        appendExecutionSummary(reflection, executedGraph);
        appendStateAnalysis(reflection, currentState);
        appendFailureAnalysis(reflection, executedGraph);
        appendRecommendations(reflection, executedGraph, currentState);
        
        return reflection.toString();
    }
    
    private void appendExecutionSummary(StringBuilder sb, IntentGraph graph) {
        if (graph == null) {
            sb.append("无执行记录\n");
            return;
        }
        
        List<IntentNode> nodes = graph.getNodes();
        int total = nodes.size();
        int completed = 0;
        int failed = 0;
        int pending = 0;
        
        for (IntentNode node : nodes) {
            switch (node.getStatus()) {
                case COMPLETED -> completed++;
                case FAILED -> failed++;
                case PENDING -> pending++;
                default -> {}
            }
        }
        
        sb.append("执行摘要:\n");
        sb.append(String.format("  - 总任务数: %d\n", total));
        sb.append(String.format("  - 已完成: %d\n", completed));
        sb.append(String.format("  - 失败: %d\n", failed));
        sb.append(String.format("  - 待执行: %d\n", pending));
        sb.append(String.format("  - 成功率: %.1f%%\n\n", 
            total > 0 ? (completed * 100.0 / total) : 0));
    }
    
    private void appendStateAnalysis(StringBuilder sb, WorldState state) {
        if (state == null) {
            sb.append("无状态信息\n");
            return;
        }
        
        PlayerState player = state.getPlayerState();
        if (player == null) {
            sb.append("无玩家状态信息\n");
            return;
        }
        
        sb.append("当前状态分析:\n");
        sb.append(String.format("  - 生命值: %.1f/%.1f (%.1f%%)\n", 
            player.getHealth(), player.getMaxHealth(), player.getHealthRatio() * 100));
        sb.append(String.format("  - 饥饿值: %.1f/%.1f (%.1f%%)\n",
            player.getHunger(), player.getMaxHunger(), player.getHungerRatio() * 100));
        sb.append(String.format("  - 位置: (%.1f, %.1f, %.1f)\n",
            player.getPositionX(), player.getPositionY(), player.getPositionZ()));
        sb.append(String.format("  - 速度: %.2f\n", player.getSpeed()));
        sb.append(String.format("  - 在地面: %s\n\n", player.isOnGround() ? "是" : "否"));
    }
    
    private void appendFailureAnalysis(StringBuilder sb, IntentGraph graph) {
        if (graph == null) {
            return;
        }
        
        List<IntentNode> failedNodes = new ArrayList<>();
        for (IntentNode node : graph.getNodes()) {
            if (node.isFailed()) {
                failedNodes.add(node);
            }
        }
        
        if (!failedNodes.isEmpty()) {
            sb.append("失败分析:\n");
            for (IntentNode node : failedNodes) {
                sb.append(String.format("  - 任务 '%s' 失败\n",
                    node.getIntent().getDescription()));
            }
            sb.append("\n");
        }
    }
    
    private void appendRecommendations(StringBuilder sb, IntentGraph graph, WorldState state) {
        sb.append("建议:\n");
        
        if (state != null && state.getPlayerState() != null) {
            PlayerState player = state.getPlayerState();
            
            if (player.getHealthRatio() < 0.5) {
                sb.append("  - 优先恢复生命值\n");
            }
            if (player.getHungerRatio() < 0.3) {
                sb.append("  - 寻找食物补充饥饿值\n");
            }
        }
        
        if (graph != null) {
            boolean hasFailed = graph.getNodes().stream().anyMatch(IntentNode::isFailed);
            if (hasFailed) {
                sb.append("  - 检查失败原因并调整策略\n");
            }
        }
        
        sb.append("\n");
    }
    
    public Map<String, Object> generateMetrics(IntentGraph graph, WorldState state) {
        Map<String, Object> metrics = new HashMap<>();
        
        if (graph != null) {
            int total = graph.getNodes().size();
            long completed = graph.getNodes().stream()
                .filter(n -> n.getStatus() == IntentNodeStatus.COMPLETED)
                .count();
            long failed = graph.getNodes().stream()
                .filter(IntentNode::isFailed)
                .count();
            
            metrics.put("totalIntents", total);
            metrics.put("completedIntents", completed);
            metrics.put("failedIntents", failed);
            metrics.put("successRate", total > 0 ? (double) completed / total : 0);
        }
        
        if (state != null && state.getPlayerState() != null) {
            PlayerState player = state.getPlayerState();
            metrics.put("healthRatio", player.getHealthRatio());
            metrics.put("hungerRatio", player.getHungerRatio());
            metrics.put("speed", player.getSpeed());
        }
        
        return metrics;
    }
}
