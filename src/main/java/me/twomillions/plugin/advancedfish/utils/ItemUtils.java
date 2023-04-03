package me.twomillions.plugin.advancedfish.utils;

import de.leonhard.storage.Yaml;
import me.twomillions.plugin.advancedfish.main;
import me.twomillions.plugin.advancedfish.manager.ConfigManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.utils
 * className:      ItemBuilder
 * date:    2022/11/5 9:54
 */
public class ItemUtils {
    // 物品构建，这并不能用于构建 Gui 物品
    public static ItemStack buildItemStack(String fileName, String ownerName, String path, String pathPrefix) {
        Yaml yaml = new Yaml(fileName, main.getInstance().getDataFolder() + "/" + path);
        yaml.setPathPrefix(pathPrefix);

        String url = yaml.getString("URL-HEAD");
        ItemStack buildItem = url.equals("") || url.equals(" ") ? new ItemStack(ItemUtils.materialValueOf(yaml.getString("MATERIAL"), fileName)) : SkullCreator.itemFromUrl(url);

        List<String> listLore = new ArrayList<>();
        ItemMeta buildItemItemMeta = buildItem.getItemMeta();

        yaml.getStringList("LORE").forEach(loreString -> {
            if (loreString.contains("<owner>") && ownerName != null && ownerName.equals("[[]]")) {
                loreString = loreString.replaceAll("<owner>", ConfigManager.getAdvancedFishYaml().getString("BOOK-PLAYER-NAME"));
                listLore.add(loreString.replaceAll("&", "§"));
            } else if (loreString.contains("<owner>") && ownerName != null) {
                loreString = loreString.replaceAll("<owner>", ownerName);
                listLore.add(loreString.replaceAll("&", "§"));
            } else {
                listLore.add(loreString.replaceAll("&", "§"));
            }
        });

        buildItem.setAmount(yaml.getInt("AMOUNT"));
        buildItem.setDurability((short) yaml.getInt("DATA"));

        if (buildItemItemMeta != null) {
            buildItemItemMeta.setDisplayName(CC.translate(yaml.getString("NAME")));
            buildItemItemMeta.setLore(listLore);

            yaml.getStringList("ENCHANT").forEach(enchantString -> {
                if (enchantString == null || enchantString.length() <= 1) return;

                String[] enchantStringSplit = enchantString.toLowerCase(Locale.ROOT).split(";");
                int enchantLevel = Integer.parseInt(enchantStringSplit[1]);

                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantStringSplit[0]));

                if (enchantment == null) {
                    CC.sendUnknownWarn("附魔", fileName, enchantStringSplit[0]);
                    return;
                }

                buildItemItemMeta.addEnchant(enchantment, enchantLevel, false);
            });

            buildItem.setItemMeta(buildItemItemMeta);
        }

        return buildItem;
    }

    // 物品相似检查
    // 通过判断名字与物品, 是否具有lore来判断相似，相比之前拥有明显性能提升
    public static boolean checkItemStackSame(ItemStack originalItem, ItemStack checkItem) {
        ItemMeta originalItemItemMeta = originalItem.getItemMeta();
        ItemMeta checkItemItemMeta = checkItem.getItemMeta();

        if (originalItemItemMeta == null || checkItemItemMeta == null || checkItemItemMeta.getLore() == null) return false;

        return checkItemItemMeta.getDisplayName().equals(originalItemItemMeta.getDisplayName()) && checkItem.getType() == originalItem.getType();
    }

    // 返回未知物品信息
    public static Material materialValueOf(String materialString, String fileName) {
        Material material;

        try { material = Material.valueOf(materialString); }
        catch (Exception exception) { CC.sendUnknownWarn("物品", fileName, materialString); return Material.AIR; }

        return material;
    }
}
