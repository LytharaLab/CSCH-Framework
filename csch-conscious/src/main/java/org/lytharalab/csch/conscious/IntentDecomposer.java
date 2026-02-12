package org.lytharalab.csch.conscious;

import org.lytharalab.csch.core.intent.*;
import org.lytharalab.csch.core.state.WorldState;
import org.lytharalab.csch.core.state.PlayerState;

import java.util.List;
import java.util.ArrayList;

public class IntentDecomposer {
    
    public List<Intent> decompose(Intent highLevelIntent, WorldState currentState) {
        List<Intent> subIntents = new ArrayList<>();
        IntentType type = highLevelIntent.getType();
        
        switch (type) {
            case NAVIGATE:
                subIntents = decomposeNavigateIntent(highLevelIntent, currentState);
                break;
            case MINE:
                subIntents = decomposeMineIntent(highLevelIntent, currentState);
                break;
            case COMBAT:
                subIntents = decomposeCombatIntent(highLevelIntent, currentState);
                break;
            case CRAFT:
                subIntents = decomposeCraftIntent(highLevelIntent, currentState);
                break;
            case BUILD:
                subIntents = decomposeBuildIntent(highLevelIntent, currentState);
                break;
            case SURVIVE:
                subIntents = decomposeSurviveIntent(highLevelIntent, currentState);
                break;
            default:
                subIntents.add(highLevelIntent);
        }
        
        return subIntents;
    }
    
    private List<Intent> decomposeNavigateIntent(Intent intent, WorldState state) {
        List<Intent> intents = new ArrayList<>();
        
        intents.add(Intent.builder()
            .description("规划路径")
            .type(IntentType.NAVIGATE)
            .parameter("action", "pathfind")
            .parameter("target", intent.getParameter("target"))
            .build());
        
        intents.add(Intent.builder()
            .description("执行移动")
            .type(IntentType.NAVIGATE)
            .parameter("action", "move")
            .build());
        
        return intents;
    }
    
    private List<Intent> decomposeMineIntent(Intent intent, WorldState state) {
        List<Intent> intents = new ArrayList<>();
        
        String resource = intent.getParameter("resource");
        int amount = intent.getParameter("amount", 1);
        
        intents.add(Intent.builder()
            .description("定位" + resource + "矿")
            .type(IntentType.EXPLORE)
            .parameter("target", resource)
            .build());
        
        intents.add(Intent.builder()
            .description("接近目标")
            .type(IntentType.NAVIGATE)
            .parameter("action", "approach")
            .build());
        
        intents.add(Intent.builder()
            .description("挖掘" + amount + "个" + resource)
            .type(IntentType.MINE)
            .parameter("resource", resource)
            .parameter("amount", amount)
            .build());
        
        return intents;
    }
    
    private List<Intent> decomposeCombatIntent(Intent intent, WorldState state) {
        List<Intent> intents = new ArrayList<>();
        
        intents.add(Intent.builder()
            .description("评估威胁")
            .type(IntentType.COMBAT)
            .parameter("action", "assess")
            .build());
        
        intents.add(Intent.builder()
            .description("选择目标")
            .type(IntentType.COMBAT)
            .parameter("action", "target")
            .build());
        
        intents.add(Intent.builder()
            .description("执行战斗")
            .type(IntentType.COMBAT)
            .parameter("action", "engage")
            .build());
        
        return intents;
    }
    
    private List<Intent> decomposeCraftIntent(Intent intent, WorldState state) {
        List<Intent> intents = new ArrayList<>();
        
        String item = intent.getParameter("item");
        
        intents.add(Intent.builder()
            .description("检查材料")
            .type(IntentType.CRAFT)
            .parameter("action", "check_materials")
            .parameter("item", item)
            .build());
        
        intents.add(Intent.builder()
            .description("打开合成界面")
            .type(IntentType.CRAFT)
            .parameter("action", "open_crafting")
            .build());
        
        intents.add(Intent.builder()
            .description("合成" + item)
            .type(IntentType.CRAFT)
            .parameter("action", "craft")
            .parameter("item", item)
            .build());
        
        return intents;
    }
    
    private List<Intent> decomposeBuildIntent(Intent intent, WorldState state) {
        List<Intent> intents = new ArrayList<>();
        
        intents.add(Intent.builder()
            .description("选择建造位置")
            .type(IntentType.BUILD)
            .parameter("action", "select_location")
            .build());
        
        intents.add(Intent.builder()
            .description("准备材料")
            .type(IntentType.GATHER)
            .parameter("action", "prepare_materials")
            .build());
        
        intents.add(Intent.builder()
            .description("执行建造")
            .type(IntentType.BUILD)
            .parameter("action", "construct")
            .build());
        
        return intents;
    }
    
    private List<Intent> decomposeSurviveIntent(Intent intent, WorldState state) {
        List<Intent> intents = new ArrayList<>();
        PlayerState player = state.getPlayerState();
        
        if (player != null) {
            if (player.getHealthRatio() < 0.5) {
                intents.add(Intent.builder()
                    .description("恢复生命值")
                    .type(IntentType.SURVIVE)
                    .parameter("action", "heal")
                    .priority(org.lytharalab.csch.core.common.Priority.HIGH)
                    .build());
            }
            
            if (player.getHungerRatio() < 0.3) {
                intents.add(Intent.builder()
                    .description("寻找食物")
                    .type(IntentType.GATHER)
                    .parameter("action", "find_food")
                    .priority(org.lytharalab.csch.core.common.Priority.HIGH)
                    .build());
            }
        }
        
        if (intents.isEmpty()) {
            intents.add(intent);
        }
        
        return intents;
    }
}
