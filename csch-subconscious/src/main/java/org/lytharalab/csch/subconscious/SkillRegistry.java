package org.lytharalab.csch.subconscious;

import org.lytharalab.csch.core.skill.SkillDefinition;
import org.lytharalab.csch.core.skill.SkillCategory;
import org.lytharalab.csch.core.skill.SkillParameter;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class SkillRegistry {
    private final Map<String, SkillDefinition> skills = new HashMap<>();
    private final Map<SkillCategory, List<String>> skillsByCategory = new HashMap<>();
    
    public void register(SkillDefinition skill) {
        if (skill == null || skill.getName() == null) {
            throw new IllegalArgumentException("Skill and skill name cannot be null");
        }
        
        skills.put(skill.getName(), skill);
        
        SkillCategory category = skill.getCategory();
        skillsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(skill.getName());
    }
    
    public void unregister(String skillName) {
        SkillDefinition removed = skills.remove(skillName);
        if (removed != null) {
            List<String> categorySkills = skillsByCategory.get(removed.getCategory());
            if (categorySkills != null) {
                categorySkills.remove(skillName);
            }
        }
    }
    
    public Optional<SkillDefinition> getSkill(String name) {
        return Optional.ofNullable(skills.get(name));
    }
    
    public boolean hasSkill(String name) {
        return skills.containsKey(name);
    }
    
    public List<SkillDefinition> getSkillsByCategory(SkillCategory category) {
        List<String> names = skillsByCategory.get(category);
        if (names == null) {
            return List.of();
        }
        return names.stream()
            .map(skills::get)
            .filter(s -> s != null)
            .toList();
    }
    
    public List<SkillDefinition> getAllSkills() {
        return new ArrayList<>(skills.values());
    }
    
    public List<String> getSkillNames() {
        return new ArrayList<>(skills.keySet());
    }
    
    public int size() {
        return skills.size();
    }
    
    public void clear() {
        skills.clear();
        skillsByCategory.clear();
    }
    
    public static SkillRegistry createDefaultRegistry() {
        SkillRegistry registry = new SkillRegistry();
        
        registry.register(SkillDefinition.builder()
            .name("NavigateTo")
            .description("导航到目标位置")
            .category(SkillCategory.NAVIGATION)
            .addParameter(SkillParameter.builder()
                .name("target")
                .type(String.class)
                .required(true)
                .description("目标位置标识")
                .build())
            .addParameter(SkillParameter.builder()
                .name("speed")
                .type(Double.class)
                .defaultValue(1.0)
                .range(0.0, 2.0)
                .description("移动速度")
                .build())
            .addParameter(SkillParameter.builder()
                .name("cautious")
                .type(Boolean.class)
                .defaultValue(false)
                .description("是否谨慎模式")
                .build())
            .build());
        
        registry.register(SkillDefinition.builder()
            .name("AlignCrosshair")
            .description("对准目标")
            .category(SkillCategory.COMBAT)
            .addParameter(SkillParameter.builder()
                .name("target")
                .type(String.class)
                .required(true)
                .description("目标标识")
                .build())
            .addParameter(SkillParameter.builder()
                .name("tolerance")
                .type(Double.class)
                .defaultValue(0.05)
                .range(0.01, 0.5)
                .description("对准容差")
                .build())
            .build());
        
        registry.register(SkillDefinition.builder()
            .name("Mine")
            .description("挖掘方块")
            .category(SkillCategory.MINING)
            .addParameter(SkillParameter.builder()
                .name("resource")
                .type(String.class)
                .required(true)
                .description("资源类型")
                .build())
            .addParameter(SkillParameter.builder()
                .name("amount")
                .type(Integer.class)
                .defaultValue(1)
                .range(1, 64)
                .description("数量")
                .build())
            .build());
        
        registry.register(SkillDefinition.builder()
            .name("CombatKite")
            .description("战斗走位")
            .category(SkillCategory.COMBAT)
            .addParameter(SkillParameter.builder()
                .name("target")
                .type(String.class)
                .required(true)
                .description("敌人标识")
                .build())
            .addParameter(SkillParameter.builder()
                .name("distance")
                .type(Double.class)
                .defaultValue(3.5)
                .range(1.0, 10.0)
                .description("保持距离")
                .build())
            .build());
        
        registry.register(SkillDefinition.builder()
            .name("Escape")
            .description("逃离危险")
            .category(SkillCategory.SURVIVAL)
            .addParameter(SkillParameter.builder()
                .name("threat")
                .type(String.class)
                .required(true)
                .description("威胁类型")
                .build())
            .addParameter(SkillParameter.builder()
                .name("minDistance")
                .type(Double.class)
                .defaultValue(10.0)
                .range(5.0, 50.0)
                .description("最小逃离距离")
                .build())
            .build());
        
        registry.register(SkillDefinition.builder()
            .name("PlaceTorch")
            .description("放置火把")
            .category(SkillCategory.BUILDING)
            .addParameter(SkillParameter.builder()
                .name("interval")
                .type(Integer.class)
                .defaultValue(7)
                .range(1, 20)
                .description("放置间隔")
                .build())
            .build());
        
        registry.register(SkillDefinition.builder()
            .name("DigTunnel")
            .description("挖掘隧道")
            .category(SkillCategory.MINING)
            .addParameter(SkillParameter.builder()
                .name("direction")
                .type(String.class)
                .required(true)
                .description("方向")
                .build())
            .addParameter(SkillParameter.builder()
                .name("length")
                .type(Integer.class)
                .defaultValue(10)
                .range(1, 100)
                .description("长度")
                .build())
            .build());
        
        return registry;
    }
}
