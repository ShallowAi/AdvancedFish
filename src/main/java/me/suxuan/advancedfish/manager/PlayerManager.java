package me.suxuan.advancedfish.manager;

import me.suxuan.advancedfish.main;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.manager
 * className:      PlayerManager
 * date:    2022/11/5 14:43
 */
public class PlayerManager {
    private static final Plugin plugin = main.getInstance();

    // 这里将记录玩家的钓鱼状态
    private static final Map<Player, Boolean> fishing = new ConcurrentHashMap<>();

    // 获取玩家的钓鱼状态
    public static boolean isPlayerFishing(Player player) {
        return fishing.getOrDefault(player, false);
    }

    // 设置玩家钓鱼的状态
    public static void setPlayerFishingStats(Player player, boolean flag) {
        fishing.put(player, flag);
    }
}
