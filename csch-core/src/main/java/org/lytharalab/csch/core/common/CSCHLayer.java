package org.lytharalab.csch.core.common;

public enum CSCHLayer {
    CONSCIOUS("Conscious", "主意识层 - LLM规划器"),
    SUBCONSCIOUS("Subconscious", "潜意识层 - 技能网络"),
    CEREBELLUM("Cerebellum", "小脑层 - 运动控制器"),
    SAFETY("Safety", "安全屏蔽层 - 动作过滤器");
    
    private final String name;
    private final String description;
    
    CSCHLayer(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
}
