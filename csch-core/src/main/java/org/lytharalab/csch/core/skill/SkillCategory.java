package org.lytharalab.csch.core.skill;

public enum SkillCategory {
    NAVIGATION("导航技能", "移动和路径规划相关技能"),
    COMBAT("战斗技能", "攻击和防御相关技能"),
    MINING("挖掘技能", "资源采集相关技能"),
    CRAFTING("合成技能", "物品制作相关技能"),
    BUILDING("建造技能", "建筑和放置相关技能"),
    SURVIVAL("生存技能", "生存维持相关技能"),
    EXPLORATION("探索技能", "探索和发现相关技能"),
    SOCIAL("社交技能", "交互和沟通相关技能"),
    GENERAL("通用技能", "通用基础技能");
    
    private final String name;
    private final String description;
    
    SkillCategory(String name, String description) {
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
