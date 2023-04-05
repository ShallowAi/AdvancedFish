package me.suxuan.advancedfish.manager;

import de.leonhard.storage.Yaml;
import me.suxuan.advancedfish.main;
import me.suxuan.advancedfish.utils.CC;
import me.suxuan.advancedfish.utils.ItemUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.manager
 * className:      BaitManager
 * date:    2022/11/5 10:55
 */
public class BaitManager {
    private static final Plugin plugin = main.getInstance();

    // 检查玩家是否可以使用此鱼饵
    public static boolean checkBait(String baitName, Player player) {
        Yaml bait = new Yaml(baitName, plugin.getDataFolder() + "/Bait");
        bait.setPathPrefix("ITEM.CONDITION");

        // 等级检查
        if (player.getLevel() < bait.getInt("LEVEL")) return false;

        // 背包物品检查
        for (String configInventoryHave : bait.getStringList("INVENTORY-HAVE")) {
            if (configInventoryHave == null || configInventoryHave.length() <= 1) continue;

            String[] item = configInventoryHave.toUpperCase(Locale.ROOT).split(";");
            int amount = Integer.parseInt(item[1]);

            int itemAmount = 0;

            // 数量检查
            for (ItemStack is : player.getInventory().all(ItemUtils.materialValueOf(item[0], baitName)).values()) {
                if (is != null && is.getType() == ItemUtils.materialValueOf(item[0], baitName)) itemAmount = itemAmount + is.getAmount();
            }

            if (!player.getInventory().contains(ItemUtils.materialValueOf(item[0], baitName)) || itemAmount < amount) return false;
        }

        // 附魔检查
        for (String configEnchantHave : bait.getStringList("ROD-HAVE-ENCHANT")) {
            if (configEnchantHave == null || configEnchantHave.length() <= 1) continue;

            String[] enchantment = configEnchantHave.toLowerCase(Locale.ROOT).split(";");
            int level = Integer.parseInt(enchantment[1]);

            ItemStack rod = player.getInventory().getItemInMainHand();
            ItemMeta rodMeta = rod.getItemMeta();

            if (rodMeta == null) return false;

            Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(enchantment[0]));

            if (ench == null) {
                CC.sendUnknownWarn("药水效果", baitName, enchantment[0]);
                return false;
            }

            if (!rod.containsEnchantment(ench) || rodMeta.getEnchantLevel(ench) < level) return false;
        }

        // 药水效果检查
        for (String configPotionEffectsHave : bait.getStringList("PLAYER-HAVE-EFFECTS")) {
            if (configPotionEffectsHave == null || configPotionEffectsHave.length() <= 1) continue;

            String[] effect = configPotionEffectsHave.toUpperCase(Locale.ROOT).split(";");

            String effectString = effect[0];
            PotionEffectType effectType = PotionEffectType.getByName(effectString);;

            int amplifier = Integer.parseInt(effect[1]);

            if (effectType == null) {
                CC.sendUnknownWarn("药水效果", baitName, effectString);
                return false;
            }

            if (!player.hasPotionEffect(effectType) || player.getPotionEffect(effectType).getAmplifier() < amplifier) return false;
        }

        return true;
    }

    // 获取所有的 Bait Item
    public static List<ItemStack> getAllBaitItems() {
        List<ItemStack> items = new ArrayList<>();
        RegisterManager.getRegisterBait().forEach(f -> items.add(getBaitItem(f, null)));

        return items;
    }

    // 获取此鱼饵的 ItemStack
    public static ItemStack getBaitItem(String baitName, String ownerName) {
        Yaml bait = new Yaml(baitName, plugin.getDataFolder() + "/Bait");
        bait.setPathPrefix("ITEM");

        String URL = bait.getString("URL-HEAD");

        return ItemUtils.buildItemStack(baitName, ownerName, "Bait", "ITEM");
    }

    // ItemStack 获取 baitName
    // 使用 map 对应查找，节省性能
    public static String baitItemToBaitName(ItemStack baitItem) {
        return RegisterManager.getItemAndBait().get(baitItem.getItemMeta().getDisplayName());
    }

    // 获取此鱼饵是否可以被吃
    // 在 0.0.9 版本过后现场阅读全部转换为容器获取
    public static boolean getBaitCanEat(String baitName) {
        return RegisterManager.getBaitEatList().contains(baitName);
    }
    
    // 获取此鱼饵是否可以被放置
    public static boolean getBaitCanBuild(String baitName) {
        return RegisterManager.getBaitBuildList().contains(baitName);
    }

    // 此鱼饵是否可以用于合成
    public static boolean getBaitCanCraft(String baitName) {
        return RegisterManager.getBaitCraftList().contains(baitName);
    }

    // 此鱼饵是否可以用于熔炉
    public static boolean getBaitCanCombustion(String baitName) {
        return RegisterManager.getBaitCombustionList().contains(baitName);
    }

    // 此鱼饵是否可以用于铁砧
    public static boolean getBaitCanAnvil(String baitName) {
        return RegisterManager.getBaitAnvilList().contains(baitName);
    }

    // 此鱼饵是否可以用于附魔
    public static boolean getBaitCanEnchant(String baitName) {
        return RegisterManager.getBaitEnchantList().contains(baitName);
    }

    // 获取制作时的替换字符串
    public static String getBaitCraftReplaceString() {
        return ConfigManager.getAdvancedFishYaml().getString("BOOK-PLAYER-NAME");
    }

    // 获取此鱼饵无法使用的消息列表
    public static List<String> getBaitCantUseMessage(String baitName) {
        Yaml bait = new Yaml(baitName, plugin.getDataFolder() + "/Bait");
        return bait.getStringList("MESSAGE");
    }
}
