package me.twomillions.plugin.advancedfish.manager;

import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import me.twomillions.plugin.advancedfish.main;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.fusesource.jansi.Ansi;

import java.io.File;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.config
 * className:      Config
 * date:    2022/10/30 13:47
 */

public class ConfigManager {
    private static final Plugin plugin = main.getInstance();

    // 创建默认配置
    public static void createDefaultConfig() {
        createYamlConfig("message", null, true);
        createYamlConfig("advancedFish", null, true);
        createYamlConfig("fishMatch", null, true);
        createYamlConfig("fishMatchCensusGui", null, true);
    }

    // 获取自定义鱼类的配置文件
    public static Yaml getAdvancedFishYaml() {
        String dataFolder = plugin.getDataFolder().toString();
        File file = new File(dataFolder, "advancedFish.yml");

        if (!file.exists()) {
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " + Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "运行有误，请检查配置文件是否被误删! 开始重新创建配置文件!");
            createYamlConfig("advancedFish", null, true);
        }

        return new Yaml("advancedFish", dataFolder);
    }

    // 获取自定义消息配置文件
    public static Yaml getMessageYaml() {
        String dataFolder = plugin.getDataFolder().toString();
        File file = new File(dataFolder, "message.yml");

        if (!file.exists()) {
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " + Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "运行有误，请检查配置文件是否被误删! 开始重新创建配置文件!");
            createYamlConfig("message", null, true);
        }

        return new Yaml("message", dataFolder);
    }

    // 获取钓鱼比赛配置文件
    public static Yaml getFishMatchYaml() {
        String dataFolder = plugin.getDataFolder().toString();
        File file = new File(dataFolder, "fishMatch.yml");

        if (!file.exists()) {
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " + Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "运行有误，请检查配置文件是否被误删! 开始重新创建配置文件!");
            createYamlConfig("fishMatch", null, true);
        }

        return new Yaml("fishMatch", dataFolder);
    }

    // 获取钓鱼比赛Gui配置文件
    public static Yaml getFishMatchCensusGuiYaml() {
        String dataFolder = plugin.getDataFolder().toString();
        File file = new File(dataFolder, "fishMatchCensusGui.yml");

        if (!file.exists()) {
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " + Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "运行有误，请检查配置文件是否被误删! 开始重新创建配置文件!");
            createYamlConfig("fishMatchCensusGui", null, true);
        }

        return new Yaml("fishMatchCensusGui", dataFolder);
    }

    // 创建指定配置文件 - Yaml
    public static void createYamlConfig(String fileName, String path, boolean inputStreamFromResource) {
        fileName = fileName + ".yml";
        String dataFolder = path == null ? plugin.getDataFolder().toString() : plugin.getDataFolder() + path;

        File file = new File(dataFolder, fileName);
        if (file.exists()) return;

        Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " + Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() +
                "检测到 " +
                Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() +
                fileName +
                Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() +
                " 配置文件为空，已自动创建并设置为更改部分自动重载。");

        if (inputStreamFromResource)
            SimplixBuilder
                    .fromFile(file)
                    .addInputStreamFromResource(fileName)
                    .setDataType(DataType.SORTED)
                    .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                    .setReloadSettings(ReloadSettings.INTELLIGENT)
                    .createYaml();
        else
            SimplixBuilder
                    .fromFile(file)
                    .setDataType(DataType.SORTED)
                    .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                    .setReloadSettings(ReloadSettings.INTELLIGENT)
                    .createYaml();
    }

    // 创建指定配置文件 - Json
    public static void createJsonConfig(String fileName, String path, boolean inputStreamFromResource) {
        fileName = fileName + ".json";
        String dataFolder = path == null ? plugin.getDataFolder().toString() : plugin.getDataFolder() + path;

        File file = new File(dataFolder, fileName);
        if (file.exists()) return;

        Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " + Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() +
                "检测到 " +
                Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() +
                fileName +
                Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() +
                " 配置文件为空，已自动创建并设置为更改部分自动重载。");

        if (inputStreamFromResource)
            SimplixBuilder
                    .fromFile(file)
                    .addInputStreamFromResource(fileName)
                    .setDataType(DataType.SORTED)
                    .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                    .setReloadSettings(ReloadSettings.INTELLIGENT)
                    .createJson();
        else
            SimplixBuilder
                    .fromFile(file)
                    .setDataType(DataType.SORTED)
                    .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                    .setReloadSettings(ReloadSettings.INTELLIGENT)
                    .createJson();
    }
}
