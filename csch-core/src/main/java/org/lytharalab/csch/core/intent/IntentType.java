package org.lytharalab.csch.core.intent;

public enum IntentType {
    NAVIGATE("导航", "移动到目标位置"),
    MINE("挖掘", "挖掘方块获取资源"),
    CRAFT("合成", "合成物品"),
    COMBAT("战斗", "与敌人战斗"),
    GATHER("采集", "采集资源"),
    BUILD("建造", "放置方块建造结构"),
    EXPLORE("探索", "探索未知区域"),
    SURVIVE("生存", "维持生存状态"),
    SOCIAL("社交", "与NPC或其他玩家交互"),
    GENERIC("通用", "通用意图类型");
    
    private final String name;
    private final String description;
    
    IntentType(String name, String description) {
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
