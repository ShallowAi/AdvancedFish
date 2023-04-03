package me.twomillions.plugin.advancedfish.runnable;

import me.twomillions.plugin.advancedfish.main;
import me.twomillions.plugin.advancedfish.manager.PlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.runnable
 * className:      CheckFishingRunnable
 * date:    2022/11/5 15:44
 */
public class CheckFishingRunnable {
    private static final Plugin plugin = main.getInstance();

    // 当玩家进来的时候创建一个 Runnable, 并通过 isPlayerFishing 来判断其状态
    // 这是为了防止玩家在钓鱼时切换物品栏或者是扔出去等操作而造成的没有设置正确的状态
    // 由于这可能有太多的意想不到的状况，所以还是 Runnable 更合适，且这不太可能造成服务器滞后
    // 当玩家离线后此循环会停止
    public static void startRunnable(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (PlayerManager.isPlayerFishing(player) && !player.getInventory().getItemInMainHand().getType().equals(Material.FISHING_ROD)) PlayerManager.setPlayerFishingStats(player, false);
            }
        }.runTaskTimerAsynchronously(plugin, 0, 5);
    }
}
