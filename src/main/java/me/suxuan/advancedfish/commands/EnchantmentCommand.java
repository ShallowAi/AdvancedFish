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
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.commands
 * className:      EnchantmentCommand
 * date:    2022/10/31 13:08
 */
public class EnchantmentCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + "此指令只有玩家可以使用。");
            return false;
        }

        Player player = (Player) sender;
        if (!player.hasPermission(ConfigManager.getAdvancedFishYaml().getString("ENCHANTMENT-PERM"))) {
            player.sendMessage(CC.translate(ConfigManager.getMessageYaml().getString("NO-PERM-MESSAGE")));
            return false;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        player.sendMessage(CC.translate("&e&o您手上的物品附魔有: &7" + item.getEnchantments()));

        return true;
    }
}
