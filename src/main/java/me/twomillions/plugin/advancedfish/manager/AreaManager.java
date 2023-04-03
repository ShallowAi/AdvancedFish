package me.twomillions.plugin.advancedfish.manager;

import de.leonhard.storage.Json;
import lombok.Getter;
import me.twomillions.plugin.advancedfish.main;
import me.twomillions.plugin.advancedfish.utils.LocationUtils;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.fusesource.jansi.Ansi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.manager
 * className:      AreaJsonManager
 * date:    2022/11/5 18:14
 */
public class AreaManager {
    private static final Plugin plugin = main.getInstance();
    @Getter private static final List<String> area = new ArrayList<>();

    // 注册所有鱼域
    public static void registerAllArea() {
        ConfigManager.getAdvancedFishYaml().getStringList("AREA").forEach(a -> {
            if (a == null || a.equals("") || a.equals(" ")) return;

            ConfigManager.createJsonConfig(a, "/Area", false);

            area.add(a);

            Bukkit.getLogger().info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                    Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() +
                    "已成功加载鱼域! 鱼域文件名称 " +
                    Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString() +
                    "-> " +
                    Ansi.ansi().fg(Ansi.Color.BLUE).boldOff().toString() +
                    a);
        });
    }

    // 添加鱼域
    public static void createArea(String areaName) {
        List<String> old = ConfigManager.getAdvancedFishYaml().getStringList("AREA");
        old.add(areaName);

        ConfigManager.getAdvancedFishYaml().set("AREA", old);
        registerAllArea();
    }

    // 删除鱼域
    public static boolean deleteArea(String areaName) {
        File file = new File(plugin.getDataFolder() + "/Area", areaName + ".json");

        if (!file.exists()) return false;

        List<String> old = ConfigManager.getAdvancedFishYaml().getStringList("AREA");
        old.remove(areaName);

        if (file.delete()) {
            Bukkit.getLogger().info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                    Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() +
                    "已删除此鱼域! 鱼域文件名称 " +
                    Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString() +
                    "-> " +
                    Ansi.ansi().fg(Ansi.Color.BLUE).boldOff().toString() +
                    areaName);
        } else {
            Bukkit.getLogger().info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                    Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() +
                    "删除失败，我们已取消此鱼域的注册，请您手动删除! 鱼域文件名称 " +
                    Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString() +
                    "-> " +
                    Ansi.ansi().fg(Ansi.Color.BLUE).boldOff().toString() +
                    areaName);

            return false;
        }

        ConfigManager.getAdvancedFishYaml().set("AREA", old);
        registerAllArea();

        return true;
    }

    // 设置鱼域名字
    public static boolean setAreaName(String areaName, String configName) {
        File file = new File(plugin.getDataFolder() + "/Area", areaName + ".json");

        if (!file.exists()) return false;

        Json json = new Json(areaName, plugin.getDataFolder() + "/Area");

        json.set("NAME", configName);

        return true;
    }

    // 设置鱼域 Max
    public static boolean setAreaMax(String areaName, Player player) {
        File file = new File(plugin.getDataFolder() + "/Area", areaName + ".json");

        if (!file.exists()) return false;

        Json json = new Json(areaName, plugin.getDataFolder() + "/Area");

        json.set("LOCATION.MAX", LocationUtils.locationToString(player.getLocation()));

        return true;
    }

    // 设置鱼域 Min
    public static boolean setAreaMin(String areaName, Player player) {
        File file = new File(plugin.getDataFolder() + "/Area", areaName + ".json");

        if (!file.exists()) return false;

        Json json = new Json(areaName, plugin.getDataFolder() + "/Area");

        json.set("LOCATION.MIN", LocationUtils.locationToString(player.getLocation()));

        return true;
    }

    // 获取鱼域名字
    public static String getAreaName(String areaName) {
        File file = new File(plugin.getDataFolder() + "/Area", areaName + ".json");

        if (!file.exists()) return null;

        Json json = new Json(areaName, plugin.getDataFolder() + "/Area");

        return json.getString("NAME");
    }

    // 获取鱼域 Max
    public static Location getAreaMax(String areaName) {
        File file = new File(plugin.getDataFolder() + "/Area", areaName + ".json");

        if (!file.exists()) return null;

        Json json = new Json(areaName, plugin.getDataFolder() + "/Area");

        return LocationUtils.stringToLocation(json.getString("LOCATION.MAX"));
    }

    // 获取鱼域 Min
    public static Location getAreaMin(String areaName) {
        File file = new File(plugin.getDataFolder() + "/Area", areaName + ".json");

        if (!file.exists()) return null;

        Json json = new Json(areaName, plugin.getDataFolder() + "/Area");

        return LocationUtils.stringToLocation(json.getString("LOCATION.MIN"));
    }

    // 是否在鱼域内
    public static boolean inArea(Location checkLocation, String areaName) {
        Location maxLocation = getAreaMax(areaName);
        Location minLocation = getAreaMin(areaName);

        if (maxLocation == null || minLocation == null) return false;

        return new IntRange(maxLocation.getX(), minLocation.getX()).containsDouble(checkLocation.getX())
                && new IntRange(maxLocation.getY(), minLocation.getY()).containsDouble(checkLocation.getY())
                &&  new IntRange(maxLocation.getZ(), minLocation.getZ()).containsDouble(checkLocation.getZ());
    }

    // 获得玩家所在的鱼域
    public static String getPlayerAreaName(Player player) {
        if (player == null) return null;

        AtomicReference<String> areaName = new AtomicReference<>();

        AreaManager.area.forEach(a -> {
            if (inArea(player.getLocation(), a)) areaName.set(a);
        });

        return areaName.get();
    }
}
