package org.lytharalab.csch.core.intent;

import java.util.Map;
import java.util.HashMap;

public class IntentEdge {
    private final String fromId;
    private final String toId;
    private final EdgeType type;
    private final Map<String, Object> conditions;
    
    public IntentEdge(String fromId, String toId) {
        this(fromId, toId, EdgeType.SEQUENCE, new HashMap<>());
    }
    
    public IntentEdge(String fromId, String toId, EdgeType type, Map<String, Object> conditions) {
        this.fromId = fromId;
        this.toId = toId;
        this.type = type != null ? type : EdgeType.SEQUENCE;
        this.conditions = conditions != null ? new HashMap<>(conditions) : new HashMap<>();
    }
    
    public String getFromId() { return fromId; }
    public String getToId() { return toId; }
    public EdgeType getType() { return type; }
    public Map<String, Object> getConditions() { return conditions; }
    
    public enum EdgeType {
        SEQUENCE("顺序执行"),
        PARALLEL("并行执行"),
        CONDITIONAL("条件执行"),
        FALLBACK("备选执行");
        
        private final String description;
        
        EdgeType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
