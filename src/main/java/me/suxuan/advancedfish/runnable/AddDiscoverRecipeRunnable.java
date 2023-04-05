package me.suxuan.advancedfish.runnable;

import me.suxuan.advancedfish.main;
import me.suxuan.advancedfish.manager.RegisterManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.runnable
 * className:      AddDiscoverRecipeRunnable
 * date:    2022/11/5 17:11
 */
public class AddDiscoverRecipeRunnable {
    private static final Plugin plugin = main.getInstance();

    // 添加自定义食谱于配方书
    public static void startRunnable(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (main.getServerVersion() < 1013) return;
                RegisterManager.getRegisterRecipe().forEach(key -> Bukkit.getScheduler().runTask(main.getInstance(), () -> player.discoverRecipe(key)));
            }
        }.runTaskAsynchronously(plugin);
    }
}
