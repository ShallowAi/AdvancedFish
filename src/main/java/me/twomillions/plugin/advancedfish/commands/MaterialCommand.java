package me.twomillions.plugin.advancedfish.commands;

import me.twomillions.plugin.advancedfish.manager.ConfigManager;
import me.twomillions.plugin.advancedfish.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.commands
 * className:      MaterialCommand
 * date:    2022/11/8 15:38
 */
public class MaterialCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + "此指令只有玩家可以使用。");
            return false;
        }

        Player player = (Player) sender;
        if (!player.hasPermission(ConfigManager.getAdvancedFishYaml().getString("MATERIAL-PERM"))) {
            player.sendMessage(CC.translate(ConfigManager.getMessageYaml().getString("NO-PERM-MESSAGE")));
            return false;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        player.sendMessage(CC.translate("&e&o您手上的物品的英文名为: &7" + item.getType()));

        return false;
    }
}
