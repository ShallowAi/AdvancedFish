package me.suxuan.advancedfish.commands;

import me.suxuan.advancedfish.main;
import me.suxuan.advancedfish.manager.AreaManager;
import me.suxuan.advancedfish.manager.ConfigManager;
import me.suxuan.advancedfish.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.commands
 * className:      AreaCommand
 * date:    2022/11/5 18:50
 */
public class AreaCommand implements TabExecutor {
    private static final Plugin plugin = main.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(" ");
            sender.sendMessage(CC.translate("&e此服务器正在使用 Advanced Fish 插件。 版本: " + plugin.getDescription().getVersion() + ", 作者: 2000000。"));
            sender.sendMessage(" ");
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(ConfigManager.getAdvancedFishYaml().getString("ADMIN-PERM"))) {
            sender.sendMessage(" ");
            sender.sendMessage(CC.translate("&e此服务器正在使用 Advanced Fish 插件。 版本: " + plugin.getDescription().getVersion() + ", 作者: 2000000。"));
            sender.sendMessage(" ");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&6&lAdvanced Fish &8- &7版本: " + plugin.getDescription().getVersion() + " &8- &f鱼域指令帮助"));
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&e • &6/area getAreaList &7- &7&o查看所有已加载鱼域."));
            player.sendMessage(CC.translate("&e • &6/area create <鱼域文件名> &7- &7&o创建一个新的鱼域."));
            player.sendMessage(CC.translate("&e • &6/area delete <鱼域文件名> &7- &7&o删除一个鱼域."));
            player.sendMessage(CC.translate("&e • &6/area setName <鱼域文件名> <鱼域名> &7- &7&o设置鱼域的名字."));
            player.sendMessage(CC.translate("&e • &6/area setMax <鱼域文件名> &7- &7&o设置鱼域的最高点."));
            player.sendMessage(CC.translate("&e • &6/area setMin <鱼域文件名> &7- &7&o设置鱼域的最低点."));
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&e • &6/af &7- &7&o基础设置."));
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&e • &c若输入后没有反应则意味着参数错误，请检查后再次尝试!"));
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&e • &6您可以查看 Wiki 获取插件的基础教程内容: https://github.com/xiaoyueyoqwq/AdvancedFish/wiki/Welcome"));
            player.sendMessage(CC.CHAT_BAR);
            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("getarealist")) {
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&e • &6目前已加载的鱼域有: " + AreaManager.getArea().toString()));
            player.sendMessage(CC.CHAT_BAR);
            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("create")) {
            if (args.length == 1) {
                player.sendMessage(CC.translate("&c您需要一个鱼域名才可以新建!"));
                return false;
            }

            AreaManager.createArea(args[1]);
            player.sendMessage(CC.translate("&a您已创建此鱼域，接下来您需要对其进行命名，使用: /Area setName 指令吧!"));
            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("delete")) {
            if (args.length == 1) {
                player.sendMessage(CC.translate("&c您需要一个鱼域名才可以删除!"));
                return false;
            }

            if (AreaManager.deleteArea(args[1]))
                player.sendMessage(CC.translate("&a已成功删除此鱼域!"));
            else
                player.sendMessage(CC.translate("&c删除出错! 请确定此鱼域是否存在!"));

            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("setname")) {
            if (args.length == 1) {
                player.sendMessage(CC.translate("&c您需要一个鱼域名才可以命名!"));
                return false;
            }

            if (args.length == 2) {
                player.sendMessage(CC.translate("&c您需要一个鱼域的名字才可以命名!"));
                return false;
            }

            if (AreaManager.setAreaName(args[1], args[2]))
                player.sendMessage(CC.translate("&a您已命名此鱼域，接下来您需要对其范围进行规定，使用: /Area setMax 指令在最高点创建一个定位点!"));
            else
                player.sendMessage(CC.translate("&c命名出错! 请确定此鱼域是否存在!"));

            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("setmax")) {
            if (args.length == 1) {
                player.sendMessage(CC.translate("&c您需要一个鱼域名才可以设置!"));
                return false;
            }

            if (AreaManager.setAreaMax(args[1], player))
                player.sendMessage(CC.translate("&a您已设置最高点，接下来您需要设置最低点，以便其构成一个类创世神的长方体，使用: /Area setMin 指令在最低点创建一个定位点!"));
            else
                player.sendMessage(CC.translate("&c设置出错! 请确定此鱼域是否存在!"));

            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("setmin")) {
            if (args.length == 1) {
                player.sendMessage(CC.translate("&c您需要一个鱼域名才可以设置!"));
                return false;
            }

            if (AreaManager.setAreaMin(args[1], player))
                player.sendMessage(CC.translate("&a恭喜您，您已设置完成!"));
            else
                player.sendMessage(CC.translate("&c设置出错! 请确定此鱼域是否存在!"));

            return true;
        }

        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission(ConfigManager.getAdvancedFishYaml().getString("ADMIN-PERM"))) return null;

        List<String> result = new ArrayList<>(AreaManager.getArea());
        Bukkit.getOnlinePlayers().forEach(p -> result.add(p.getName()));
        return result;
    }
}
