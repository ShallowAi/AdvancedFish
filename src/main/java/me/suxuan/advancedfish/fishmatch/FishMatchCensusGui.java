package me.suxuan.advancedfish.fishmatch;

import de.leonhard.storage.Yaml;
import me.suxuan.advancedfish.main;
import me.suxuan.advancedfish.manager.ConfigManager;
import me.suxuan.advancedfish.utils.CC;
import me.suxuan.advancedfish.utils.ItemUtils;
import me.suxuan.advancedfish.utils.SkullCreator;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.fishmatch
 * className:      FishMatchGui
 * date:    2022/11/9 18:15
 */
public class FishMatchCensusGui {
    private static final Plugin plugin = main.getInstance();

    // 打开 Gui
    public static void openGui(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
           Inventory chest = createCensusGui(player);

           // 如果这个 GUI 已经过期了
           if (chest == null) {
               ConfigManager.getMessageYaml().getStringList("CENSUS-GUI-OVERDUE").forEach(m -> Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(CC.translate(m))));
               return;
           }

           Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(chest));
        });
    }

    // 遍历配置文件创建 Gui
    private static Inventory createCensusGui(Player player) {
        if (FishMatchManager.playerFishMatchIntegral.size() < 1) return null;

        Yaml yaml = ConfigManager.getFishMatchCensusGuiYaml();
        Inventory censusGui = Bukkit.createInventory(player, yaml.getInt("SIZE") * 9, CC.translate(ConfigManager.getFishMatchCensusGuiYaml().getString("TITLE")));
        String inventoryPlayerName = player.getName();

        // 物品构造器
        // 由于这一部分过于复杂，且只有这一处使用，并没有将其写入到 ItemUtils 内
        yaml.getStringList("ITEM.SLOT").forEach(configItemSlot -> {
            String[] getCensusGuiItems = configItemSlot.split(";");
            String number = getCensusGuiItems[0];

            yaml.setPathPrefix(number);
            int slot = Integer.parseInt(getCensusGuiItems[1]);

            ItemStack guiItem;
            String url = yaml.getString("URL-HEAD");
            String materialString = yaml.getString("MATERIAL");
            String configCensusString = yaml.getString("CENSUS");

            // 获取物品类型
            if (materialString.toUpperCase(Locale.ROOT).equals("HEAD")) {
                String censusGetPlayer = NumberUtils.isNumber(configCensusString) ? FishMatchManager.censusGetPlayer(Integer.parseInt(configCensusString)) : inventoryPlayerName;

                if (censusGetPlayer == null || censusGetPlayer.equals("")) {
                    censusGui.setItem(slot, getNotHaveItem());
                    return;
                } else guiItem = SkullCreator.itemFromName(censusGetPlayer);

            } else guiItem = url.equals("") || url.equals(" ") ? new ItemStack(ItemUtils.materialValueOf(materialString, yaml.getName())) : SkullCreator.itemFromUrl(url);

            // 获取排名等数据
            String playerName;
            String playerIntegral;
            String playerRanking = null;

            // 如果此物品 CENSUS 是 me
            if (configCensusString.toLowerCase(Locale.ROOT).equals("me")) {
                Integer integer = FishMatchManager.playerNameGetCensus(inventoryPlayerName);
                playerRanking = integer == null ? null : Integer.toString(integer);
                playerName = player.getName();

                // 如果此玩家没有排名信息，这只有一种可能 就是此玩家没有参与比赛
                if (playerRanking == null) {
                    yaml.setPathPrefix(null);
                    playerIntegral = playerRanking = yaml.getString("NOT-PARTICIPATING");
                } else playerIntegral = String.valueOf(FishMatchManager.censusGetIntegral(Integer.parseInt(playerRanking)));

            } else {
                int configCensusInt = Integer.parseInt(configCensusString);
                playerName = FishMatchManager.censusGetPlayer(configCensusInt);

                Double integer = FishMatchManager.censusGetIntegral(configCensusInt);
                playerIntegral = integer == null ? null : Double.toString(integer);
            }

            if (playerName == null || playerIntegral == null) {
                censusGui.setItem(slot, getNotHaveItem());
                return;
            }

            yaml.setPathPrefix(number);
            List<String> newLore = new ArrayList<>();
            ItemMeta guiItemMeta = guiItem.getItemMeta();

            // 对 newLore 进行颜色字符替换与变量替换
            for (String oldLore : yaml.getStringList("LORE")) {
                if (oldLore.contains("<name>")) oldLore = oldLore.replaceAll("<name>", playerName);
                if (oldLore.contains("<integral>")) oldLore = oldLore.replaceAll("<integral>", playerIntegral);
                if (oldLore.contains("<ranking>")) oldLore = oldLore.replaceAll("<ranking>", playerRanking);

                newLore.add(oldLore.replaceAll("&", "§"));
            }

            // 基础设置
            guiItem.setAmount(yaml.getInt("AMOUNT"));
            guiItem.setDurability((short) yaml.getInt("DATA"));

            if (guiItemMeta != null) {
                guiItemMeta.setDisplayName(CC.translate(yaml.getString("NAME")));
                guiItemMeta.setLore(newLore);

                yaml.getStringList("ENCHANT").forEach(enchantString -> {
                    if (enchantString == null || enchantString.length() <= 1) return;

                    String[] enchantStringSplit = enchantString.toLowerCase(Locale.ROOT).split(";");
                    int enchantLevel = Integer.parseInt(enchantStringSplit[1]);

                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantStringSplit[0]));

                    if (enchantment == null) {
                        CC.sendUnknownWarn("附魔", yaml.getName(), enchantStringSplit[0]);
                        return;
                    }

                    guiItemMeta.addEnchant(enchantment, enchantLevel, false);
                });

                guiItem.setItemMeta(guiItemMeta);
            }

            censusGui.setItem(slot, guiItem);
        });

        return censusGui;
    }

    // 构建 Not Have 物品
    private static ItemStack getNotHaveItem() {
        Yaml yaml = ConfigManager.getFishMatchCensusGuiYaml();

        yaml.setPathPrefix("NOT-HAVE");

        String url = yaml.getString("URL-HEAD");
        ItemStack notHaveItem = url.equals("") || url.equals(" ") ? new ItemStack(ItemUtils.materialValueOf(yaml.getString("MATERIAL"), yaml.getName())) : SkullCreator.itemFromUrl(url);

        ItemMeta notHaveItemMeta = notHaveItem.getItemMeta();
        List<String> newLore = new ArrayList<>();

        if (notHaveItemMeta == null) return new ItemStack(Material.AIR);

        notHaveItemMeta.setDisplayName(CC.translate(yaml.getString("NAME")));
        yaml.getStringList("LORE").forEach(l -> newLore.add(l.replace("&", "§")));
        notHaveItemMeta.setLore(newLore);
        notHaveItem.setDurability((short) yaml.getInt("DATA"));
        notHaveItem.setAmount(yaml.getInt("AMOUNT"));

        yaml.getStringList("ENCHANT").forEach(enchantString -> {
            if (enchantString == null || enchantString.length() <= 1) return;

            String[] enchantStringSplit = enchantString.toLowerCase(Locale.ROOT).split(";");
            int level = Integer.parseInt(enchantStringSplit[1]);

            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantStringSplit[0]));

            if (enchantment == null) {
                CC.sendUnknownWarn("附魔", yaml.getName(), enchantStringSplit[0]);
                return;
            }

            notHaveItemMeta.addEnchant(enchantment, level, false);
        });

        notHaveItem.setItemMeta(notHaveItemMeta);

        return notHaveItem;
    }
}
