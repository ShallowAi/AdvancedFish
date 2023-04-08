package me.suxuan.advancedfish.utils;

import me.suxuan.advancedfish.fishmatch.FishMatchManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.fusesource.jansi.Ansi;

/**
 * @author: CBer_SuXuan
 * @project: AdvancedFish
 * @className: CC
 * @date: 2023/4/5 19:24
 * @description: Chat Control
 */
public class CC {
    // LONG CHAT BAR!
    public static final String CHAT_BAR = org.bukkit.ChatColor.GRAY.toString() + org.bukkit.ChatColor.STRIKETHROUGH + "------------------------------------------------";

    // translate & into §
    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    // Fast replace message
    public static String replaceAndTranslate(String message, Player player, Player replacePlayer, Integer countDown, Double nowInteger) {
        if (message.contains("<player>") && player != null) message = message.replaceAll("<player>", player.getName());
        if (message.contains("<s>") && countDown != null) message = message.replaceAll("<s>", countDown.toString());
        if (message.contains("<rplayer>") && replacePlayer != null) message = message.replaceAll("<rplayer>", replacePlayer.getName());
        if (message.contains("<nowinteger>") && nowInteger != null && player != null && FishMatchManager.isFishMatchPlayerDataExists(player)) message = message.replaceAll("<nowinteger>", nowInteger.toString());

        return CC.translate(message);
    }

    // Fast return wrong message
    public static void sendUnknownWarn(String unknown, String fileName, String unknownName) {
        Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() +
                "您填入了一个未知的" + unknown + "，位于 -> " +
                Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() +
                fileName +
                Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() +
                "，您填入的未知" + unknown + "为 -> " +
                Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() +
                unknownName);
    }

    public static void loggerCommandWrongMessage(String message) {
        Bukkit.getLogger().info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + message);
    }

    public static void loggerCommandCorrectMessage(String message) {
        Bukkit.getLogger().info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + message);
    }
}
