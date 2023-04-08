package me.suxuan.advancedfish.commands;

import me.suxuan.advancedfish.manager.ConfigManager;
import me.suxuan.advancedfish.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.PotionMeta;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;

/**
 * @author: CBer_SuXuan
 * @project: AdvancedFish
 * @className: PotionEffectCommand
 * @date: 2023/4/5 23:09
 * @description: Get potion on hand
 */
public class PotionEffectCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + "此指令只有玩家可以使用。");
            return false;
        }

        if (!player.hasPermission(ConfigManager.getAdvancedFishYaml().getString("POTION-EFFECT-PERM"))) {
            player.sendMessage(CC.translate(ConfigManager.getMessageYaml().getString("NO-PERM-MESSAGE")));
            return false;
        }

        PotionMeta meta;

        // 直接判断是不是药水的类型太多了，直接捕获 ClassCastException 异常并返回负面消息
        try {
            meta = (PotionMeta) player.getInventory().getItemInMainHand().getItemMeta();
        } catch (ClassCastException e) {
            player.sendMessage(CC.translate("&c您手中的物品有误"));
            return false;
        }

        if (meta == null) {
            player.sendMessage(CC.translate("&c您手中的物品有误"));
            return false;
        }

        // 返回 PotionEffectType 名称
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&e&o您手中的物品药水效果: &7" +
                meta.getBasePotionData().getType().getEffectType().getName()));
        player.sendMessage(CC.CHAT_BAR);

        return true;
    }
}
