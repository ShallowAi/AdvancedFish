package me.suxuan.advancedfish.runnable;

import me.suxuan.advancedfish.main;
import me.suxuan.advancedfish.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: CBer_SuXuan
 * @project: AdvancedFish
 * @className: UpdateCheckerRunnable
 * @date: 2023/4/5 18:52
 * @description: Check plugin version
 */
public class UpdateCheckerRunnable {
    private static final Plugin plugin = main.getInstance();
    public static boolean isLatestVersion = true;

    // 异步网页更新检查
    public static void startRunnable() {
        if (main.isDisabled()) return;

        if (!ConfigManager.getAdvancedFishYaml().getBoolean("UPDATE-CHECKER")) return;
        int cycle = ConfigManager.getAdvancedFishYaml().getInt("CHECK-CYCLE");

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            String urlString = getLatestVersion();

            if (Objects.equals(urlString, plugin.getDescription().getVersion())) {
                isLatestVersion = true;

                Bukkit.getLogger().info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                        Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() +
                        "自动更新检查完成，您目前正在使用最新版的 Advanced Fish! 版本: " + plugin.getDescription().getVersion());
            } else if (urlString.equals("")) {
                isLatestVersion = false;
            } else {
                isLatestVersion = false;

                Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                        Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() +
                        "您目前正在使用过时的 Advanced Fish! 请更新以避免服务器出现问题! " +
                        "下载链接: https://github.com/xiaoyueyoqwq/AdvancedFish/releases");

            }
        }, 0, (long) cycle * 1200);
    }

    // Get plugin latest version
    private static String getLatestVersion() {
        String sb;
        try {
            for (Scanner sc = new Scanner(new URL("https://ifcserver.club/advancedfishupdate/check.html").openStream()); sc.hasNext();) {
                sb = sc.nextLine();
                Pattern ptn = Pattern.compile("(?<=>)(.+?)(?=<)");
                Matcher mat = ptn.matcher(sb);
                if (mat.find()) {
                    return mat.group(1);
                }
            }
        } catch (IOException e) {
            isLatestVersion = false;
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                    Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "Advanced Fish 更新检查错误... 请务必手动检查插件是否为最新版。 " +
                    "下载链接: https://github.com/xiaoyueyoqwq/AdvancedFish/releases");
        }
        return "";
    }
}

