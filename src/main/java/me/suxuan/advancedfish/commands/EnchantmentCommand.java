package me.suxuan.advancedfish.commands;

import me.suxuan.advancedfish.manager.ConfigManager;
import me.suxuan.advancedfish.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: CBer_SuXuan
 * @project: AdvancedFish
 * @className: EnchantmentCommand
 * @date: 2023/4/5 22:22
 * @description: Get enchantment on hand
 */
public class EnchantmentCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + "此指令只有玩家可以使用。");
            return false;
        }

        if (!player.hasPermission(ConfigManager.getAdvancedFishYaml().getString("ENCHANTMENT-PERM"))) {
            player.sendMessage(CC.translate(ConfigManager.getMessageYaml().getString("NO-PERM-MESSAGE")));
            return false;
        }

        List<String> enchant = new ArrayList<>();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().equals(Material.ENCHANTED_BOOK)) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            if (meta.getStoredEnchants().isEmpty())  {
                player.sendMessage(CC.CHAT_BAR);
                player.sendMessage(CC.translate("&e&o您手上的物品没有附魔"));
                player.sendMessage(CC.CHAT_BAR);
                return true;
            }
            for (Map.Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet())
                enchant.add(entry.getKey().getKey().getKey());
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&e&o您手上的物品附魔有: &7" + enchant));
            player.sendMessage(CC.CHAT_BAR);
            return true;
        } else {
            if (item.getEnchantments().isEmpty()) {
                player.sendMessage(CC.CHAT_BAR);
                player.sendMessage(CC.translate("&e&o您手上的物品没有附魔"));
                player.sendMessage(CC.CHAT_BAR);
                return true;
            }
            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet())
                enchant.add(entry.getKey().getKey().getKey());
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&e&o您手上的物品附魔有: &7" + enchant));
            player.sendMessage(CC.CHAT_BAR);
        }
        return true;
    }
}