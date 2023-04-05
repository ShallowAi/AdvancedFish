package me.suxuan.advancedfish.manager;

import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import me.suxuan.advancedfish.main;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.fusesource.jansi.Ansi;

import java.io.File;
/**
 * @author: CBer_SuXuan
 * @project: AdvancedFish
 * @className: ConfigManager
 * @date: 2023/4/5 12:01
 * @description: Manage Config
 */
public class ConfigManager {
    private static final Plugin plugin = main.getInstance();

    // Create default config
    public static void createDefaultConfig() {
        createYamlConfig("message", null, true);
        createYamlConfig("advancedFish", null, true);
        createYamlConfig("fishMatch", null, true);
        createYamlConfig("fishMatchCensusGui", null, true);
    }

    // Get advancedFish.yml file
    public static Yaml getAdvancedFishYaml() {
        String dataFolder = plugin.getDataFolder().toString();
        File file = new File(dataFolder, "advancedFish.yml");
        if (!file.exists()) {
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] "
                    + Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "运行有误，请检查配置文件是否被误删! 开始重新创建配置文件!");
            createYamlConfig("advancedFish", null, true);
        }
        return new Yaml("advancedFish", dataFolder);
    }

    // Get message.yml file
    public static Yaml getMessageYaml() {
        String dataFolder = plugin.getDataFolder().toString();
        File file = new File(dataFolder, "message.yml");
        if (!file.exists()) {
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] "
                    + Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "运行有误，请检查配置文件是否被误删! 开始重新创建配置文件!");
            createYamlConfig("message", null, true);
        }
        return new Yaml("message", dataFolder);
    }

    // Get fishMatch.yml file
    public static Yaml getFishMatchYaml() {
        String dataFolder = plugin.getDataFolder().toString();
        File file = new File(dataFolder, "fishMatch.yml");
        if (!file.exists()) {
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] "
                    + Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "运行有误，请检查配置文件是否被误删! 开始重新创建配置文件!");
            createYamlConfig("fishMatch", null, true);
        }
        return new Yaml("fishMatch", dataFolder);
    }

    // Get fishMatchCensusGui.yml file
    public static Yaml getFishMatchCensusGuiYaml() {
        String dataFolder = plugin.getDataFolder().toString();
        File file = new File(dataFolder, "fishMatchCensusGui.yml");
        if (!file.exists()) {
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] "
                    + Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "运行有误，请检查配置文件是否被误删! 开始重新创建配置文件!");
            createYamlConfig("fishMatchCensusGui", null, true);
        }
        return new Yaml("fishMatchCensusGui", dataFolder);
    }

    // Create specify YML file
    public static void createYamlConfig(String fileName, String path, boolean inputStreamFromResource) {
        fileName = fileName + ".yml";
        String dataFolder = path == null ? plugin.getDataFolder().toString() : plugin.getDataFolder() + path;

        File file = new File(dataFolder, fileName);
        if (file.exists()) return;

        Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] "
                + Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "检测到 "
                + Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + fileName +
                Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + " 配置文件为空，已自动创建并设置为更改部分自动重载。");
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

    // Create specify JSON file
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