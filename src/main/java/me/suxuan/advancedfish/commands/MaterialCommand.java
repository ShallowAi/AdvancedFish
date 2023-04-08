package me.suxuan.advancedfish.commands;

import me.suxuan.advancedfish.manager.ConfigManager;
import me.suxuan.advancedfish.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;

/**
 * @author: CBer_SuXuan
 * @project: AdvancedFish
 * @className: MaterialCommand
 * @date: 2023/4/6 9:30
 * @description: Get material on hand
 */
public class MaterialCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player player)) {
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + "此指令只有玩家可以使用。");
            return false;
        }

        if (!player.hasPermission(ConfigManager.getAdvancedFishYaml().getString("MATERIAL-PERM"))) {
            player.sendMessage(CC.translate(ConfigManager.getMessageYaml().getString("NO-PERM-MESSAGE")));
            return false;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&e&o您手上的物品的英文名为: &7" + item.getType()));
        player.sendMessage(CC.CHAT_BAR);

        return false;
    }
}
