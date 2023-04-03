package me.twomillions.plugin.advancedfish;

import lombok.Getter;
import lombok.Setter;
import me.twomillions.plugin.advancedfish.fishmatch.FishMatchManager;
import me.twomillions.plugin.advancedfish.manager.AreaManager;
import me.twomillions.plugin.advancedfish.manager.ConfigManager;
import me.twomillions.plugin.advancedfish.manager.RegisterManager;
import me.twomillions.plugin.advancedfish.runnable.CheckFishingRunnable;
import me.twomillions.plugin.advancedfish.runnable.UpdateCheckerRunnable;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;

import java.util.Arrays;

public final class main extends JavaPlugin {
    // volatile 防止线程直接共享变量可能会有值更新不可见的问题
    @Getter @Setter private volatile static main instance;
    @Getter @Setter private volatile static Double serverVersion;

    @Getter @Setter private volatile static boolean disabled;

    @Override
    public void onEnable() {
        setInstance(this);
        setDisabled(false);

        // 获取 -> org.bukkit.craftbukkit.v1_7_R4
        // 分割后为 -> 1_7, 最终为 -> 107
        // 1.12.2 -> 101202 1.19.2 -> 101902 这里把 _ 换成 0 是为了放置 1.19 比 1.7 小的问题
        setServerVersion(Double.parseDouble(Arrays.toString(StringUtils.substringsBetween(getServer().getClass().getPackage().getName(), ".v", "_R"))
                .replace("_", "0").replace("[", "").replace("]", "")));

        Bukkit.getLogger().info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() +
                "Advanced Fish 插件已成功加载! 感谢您使用此插件! 版本: " +
                main.getInstance().getDescription().getVersion() +
                ", 作者: 2000000。");

        // 注册
        ConfigManager.createDefaultConfig();
        AreaManager.registerAllArea();
        RegisterManager.registerAllFish();
        RegisterManager.registerAllBait();

        // 开启时删除防止意外崩溃而保留的临时钓鱼比赛玩家数据
        FishMatchManager.deleteFishMatchPlayerDataDirectory();

        // 监听器与指令注册
        RegisterManager.registerListener();
        RegisterManager.registerCommands();

        // bStats
        if (ConfigManager.getAdvancedFishYaml().getBoolean("BSTATS")) {
            int pluginId = 16770; // <-- Replace with the id of your plugin!
            bStats metrics = new bStats(this, pluginId);
        }

        // 网页更新
        UpdateCheckerRunnable.startRunnable();

        // 这里是热重载
        // 如果玩家没有使用插件的指令进行热重载，那么会导致 CheckFishingRunnable 停止
        // 所以这里检查服内是否有此玩家，如果有的话那么就为所有玩家启动 CheckFishingRunnable
        if (Bukkit.getOnlinePlayers().size() != 0) Bukkit.getOnlinePlayers().forEach(CheckFishingRunnable::startRunnable);

        // 负面消息
        if (getServerVersion() <= 1012) {
            Bukkit.getLogger().info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                    Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() +
                    "Advanced Fish 正在一个不兼容的版本是运行，在低版本中没有 REEL_IN 这个状态，相同的情况下触发的是 FAILED_ATTEMPT，由于技术问题此插件不会再兼容低版本。插件即将卸载。");

            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        setDisabled(true);

        // 删除钓鱼比赛的玩家数据临时 Json 文件
        FishMatchManager.deleteFishMatchPlayerDataDirectory();
        // 卸载已有配方以防止插件重载导致的配方重复错误
        RegisterManager.getRegisterRecipe().forEach(key -> {
            if (main.getServerVersion() >= 1014) Bukkit.removeRecipe(key);
            if (main.getServerVersion() >= 1013) Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.undiscoverRecipe(key));
        });
    }
}
