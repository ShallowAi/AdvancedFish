package me.suxuan.advancedfish.commands;

import me.suxuan.advancedfish.main;
import me.suxuan.advancedfish.manager.BaitManager;
import me.suxuan.advancedfish.manager.FishManager;
import me.suxuan.advancedfish.manager.RegisterManager;
import me.suxuan.advancedfish.utils.CC;
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
 * className:      GiveAdvancedFishItemCommand
 * date:    2022/11/9 17:49
 */
public class ConsoleCommand implements CommandExecutor {
    private static final Plugin plugin = main.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage(CC.translate("&c您无法使用此指令，此指令只能由控制台使用。"));
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(CC.CHAT_BAR);
            sender.sendMessage(CC.translate("&6&lAdvanced Fish &8- &7版本: " + plugin.getDescription().getVersion() + " &8- &f控制台指令帮助"));
            sender.sendMessage(CC.CHAT_BAR);
            sender.sendMessage(CC.translate("&e • &6/afc getFishList &7- &7&o获取所有的已加载鱼类."));
            sender.sendMessage(CC.translate("&e • &6/afc getFishItem <鱼类文件名> <自定义的钓手名> <给予的玩家> &7- &7&o获取指定鱼类的物品."));
            sender.sendMessage(CC.translate("&e • &6/afc getFurnaceFishItem <鱼类文件名> <自定义的钓手名> <给予的玩家> &7- &7&o获取指定鱼类烹饪后的物品."));
            sender.sendMessage(CC.CHAT_BAR);
            sender.sendMessage(CC.translate("&e • &6/afc getBaitList &7- &7&o获取所有的已加载鱼饵."));
            sender.sendMessage(CC.translate("&e • &6/afc getBaitItem <鱼饵文件名> <自定义的制作者名> <给予的玩家> &7- &7&o获取指定鱼饵的物品."));
            sender.sendMessage(CC.CHAT_BAR);
            sender.sendMessage(CC.translate("&e • &6/afc reload &7- &7&o重新注册所有内容 (自动重载内容请详细查看鱼类配置文件)"));
            sender.sendMessage(CC.CHAT_BAR);
            sender.sendMessage(CC.translate("&e • &6您可以查看 Wiki 获取插件的基础教程内容: https://github.com/xiaoyueyoqwq/AdvancedFish/wiki/Welcome"));
            sender.sendMessage(CC.CHAT_BAR);
            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("getfishlist")) {
            sender.sendMessage(CC.CHAT_BAR);
            sender.sendMessage(CC.translate("&e • &6目前已加载的鱼类有: " + RegisterManager.getRegisterFish().toString()));
            sender.sendMessage(CC.CHAT_BAR);
            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("getfishitem")) {
            if (args.length == 1) {
                sender.sendMessage("您需要一个鱼的名字才能获得所对应的物品!");
                return false;
            }

            if (args.length == 2) {
                sender.sendMessage("您需要自定义一个钓手名才能获取!");
                return false;
            }

            if (args.length == 3) {
                sender.sendMessage("您需要指定一个玩家才能给予物品!");
                return false;
            }

            Player player = Bukkit.getPlayerExact(args[3]);

            if (player == null) {
                sender.sendMessage("此玩家不在线!");
                return false;
            }

            player.getInventory().addItem(FishManager.getFishItem(args[1], args[2]));
            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("getfurnacefishitem")) {
            if (args.length == 1) {
                sender.sendMessage(CC.translate("&c您需要一个鱼的名字才能获得所对应的物品!"));
                return false;
            }

            if (args.length == 2) {
                sender.sendMessage(CC.translate("&c您需要自定义一个钓手名才能获取!"));
                return false;
            }

            if (!FishManager.getFishEnableFurnaceRecipe(args[1])) {
                sender.sendMessage(CC.translate("&c此鱼并没有设置其可烹饪，您无法获取!"));
                return false;
            }

            if (args.length == 3) {
                sender.sendMessage("您需要指定一个玩家才能给予物品!");
                return false;
            }

            Player player = Bukkit.getPlayerExact(args[3]);

            if (player == null) {
                sender.sendMessage("此玩家不在线!");
                return false;
            }

            player.getInventory().addItem(FishManager.fishItemToFurnaceItem(FishManager.getFishItem(args[1], sender.getName()), args[2]));
            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("getbaitlist")) {
            sender.sendMessage(CC.CHAT_BAR);
            sender.sendMessage(CC.translate("&e • &6目前已加载的鱼饵有: " + RegisterManager.getRegisterBait().toString()));
            sender.sendMessage(CC.CHAT_BAR);
            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("getbaititem")) {
            if (args.length == 1) {
                sender.sendMessage(CC.translate("&c您需要一个鱼饵的名字才能获得所对应的物品!"));
                return false;
            }

            if (args.length == 2) {
                sender.sendMessage(CC.translate("&c您需要自定义一个制作人名才能获取!"));
                return false;
            }

            if (args.length == 3) {
                sender.sendMessage("您需要指定一个玩家才能给予物品!");
                return false;
            }

            Player player = Bukkit.getPlayerExact(args[3]);

            if (player == null) {
                sender.sendMessage("此玩家不在线!");
                return false;
            }

            player.getInventory().addItem(BaitManager.getBaitItem(args[1], args[2]));
            return true;
        }

        if (args[0].toLowerCase(Locale.ROOT).equals("reload")) {
            // 卸载已有配方后再添加以防止插件重载导致的配方重复错误
            RegisterManager.getRegisterRecipe().forEach(key -> {
                if (main.getServerVersion() >= 1014) Bukkit.removeRecipe(key);
                if (main.getServerVersion() >= 1013) Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.undiscoverRecipe(key));
            });

            RegisterManager.registerAllFish();
            RegisterManager.registerAllBait();

            sender.sendMessage("重载完成!");
            return true;
        }

        return false;
    }
}
