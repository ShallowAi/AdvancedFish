package me.twomillions.plugin.advancedfish.commands;

import me.twomillions.plugin.advancedfish.fishmatch.FishMatchManager;
import me.twomillions.plugin.advancedfish.fishmatch.FishMatchState;
import me.twomillions.plugin.advancedfish.main;
import me.twomillions.plugin.advancedfish.manager.ConfigManager;
import me.twomillions.plugin.advancedfish.runnable.FishMatchRunnable;
import me.twomillions.plugin.advancedfish.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.commands
 * className:      FishMatchCommand
 * date:    2022/11/9 13:11
 */
public class FishMatchCommand implements CommandExecutor {
    private static final Plugin plugin = main.getInstance();
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(" ");
            sender.sendMessage(CC.translate("&e此服务器正在使用 Advanced Fish 插件。 版本: " + plugin.getDescription().getVersion() + ", 作者: 2000000。"));
            sender.sendMessage(" ");
            return false;
        }

        Player player = (Player) sender;
        String holdPerm = ConfigManager.getAdvancedFishYaml().getString("FISH-MATCH-HOLD");
        String setPerm = ConfigManager.getAdvancedFishYaml().getString("FISH-MATCH-SET");

        // 根据不同的权限发送不同的内容
        if (args.length == 0) {
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&6&lAdvanced Fish &8- &7版本: " + plugin.getDescription().getVersion() + " &8- &f比赛指令帮助"));
            player.sendMessage(CC.CHAT_BAR);

            if (player.hasPermission(holdPerm)) player.sendMessage(CC.translate("&e • &6/fishMatch start &7- &7&o举办一个钓鱼比赛."));

            player.sendMessage(CC.translate("&e • &6/fishMatch join &7- &7&o加入钓鱼比赛."));
            player.sendMessage(CC.translate("&e • &6/fishMatch leave &7- &7&o退出钓鱼比赛."));

            if (player.hasPermission(setPerm)) {
                player.sendMessage(CC.CHAT_BAR);
                player.sendMessage(CC.translate("&e • &6/fishMatch set <玩家> <积分> &7- &7&o设置一个玩家的钓鱼积分."));
            }

            player.sendMessage(CC.CHAT_BAR);
            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("start")) {
            if (!player.hasPermission(holdPerm)) {
                player.sendMessage(CC.translate(ConfigManager.getMessageYaml().getString("NO-PERM-MESSAGE")));
                return false;
            }

            FishMatchManager.createFishMatch(player);
            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("join")) {
            if (FishMatchManager.getFishMatchState() == FishMatchState.NONE) {
                ConfigManager.getMessageYaml().getStringList("MATCH-NOT-HELD").forEach(m -> player.sendMessage(CC.translate(m)));
                return false;
            }

            if (FishMatchManager.getFishMatchState() != FishMatchState.HOLD && !ConfigManager.getFishMatchYaml().getBoolean("JOIN-AFTER-START")) {
                ConfigManager.getMessageYaml().getStringList("CANT-JOIN-AFTER-START").forEach(m -> player.sendMessage(CC.translate(m)));
                return false;
            }

            if (FishMatchManager.isPlayerInTheFishMatch(player)) {
                ConfigManager.getMessageYaml().getStringList("JOINED-BUT-USE-JOIN-COMMAND").forEach(m -> player.sendMessage(CC.translate(m)));
                return false;
            }

            FishMatchRunnable.startRunnable(player, FishMatchState.PLAYER_JOIN, null);
            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("leave")) {
            if (FishMatchManager.getFishMatchState() == FishMatchState.NONE) {
                ConfigManager.getMessageYaml().getStringList("MATCH-NOT-HELD-BUT-LEAVE").forEach(m -> player.sendMessage(CC.translate(m)));
                return false;
            }

            if (!FishMatchManager.isPlayerInTheFishMatch(player)) {
                ConfigManager.getMessageYaml().getStringList("NOT-IN-MATCH-BUT-USE-LEAVE-COMMAND").forEach(m -> player.sendMessage(CC.translate(m)));
                return false;
            }

            FishMatchRunnable.startRunnable(player, FishMatchState.PLAYER_LEAVE, null);
            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("set")) {
            if (!player.hasPermission(setPerm)) {
                player.sendMessage(CC.translate(ConfigManager.getMessageYaml().getString("NO-PERM-MESSAGE")));
                return false;
            }

            if (FishMatchManager.getFishMatchState() == FishMatchState.NONE) {
                ConfigManager.getMessageYaml().getStringList("MATCH-NOT-HELD-BUT-SET-INTEGRAL").forEach(m -> player.sendMessage(CC.translate(m)));
                return false;
            }

            if (args.length == 1) {
                player.sendMessage(CC.translate("&c您需要一个玩家的名字才可以添加!"));
                return false;
            }

            if (args.length == 2) {
                player.sendMessage(CC.translate("&c您需要填写添加的数量!"));
                return false;
            }

            Player setPlayer = Bukkit.getPlayer(args[1]);

            if (setPlayer == null) {
                sender.sendMessage("此玩家不在线!");
                return false;
            }

            FishMatchManager.setFishMatchPlayerIntegral(setPlayer, Integer.parseInt(args[2]));
            player.sendMessage(CC.translate("&a设置完成!"));
            return true;
        }

        return false;
    }
}
