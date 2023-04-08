package me.suxuan.advancedfish.utils;

import de.leonhard.storage.Yaml;
import me.suxuan.advancedfish.main;
import me.suxuan.advancedfish.manager.ConfigManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author: CBer_SuXuan
 * @project: AdvancedFish
 * @className: ItemUtils
 * @date: 2023/4/5 19:11
 * @description: Build items
 */
public class ItemUtils {
    // Build items, instead of building GUI items
    public static ItemStack buildItemStack(String fileName, String ownerName, String path, String pathPrefix) {
        Yaml yaml = new Yaml(fileName, main.getInstance().getDataFolder() + "/" + path);
        yaml.setPathPrefix(pathPrefix);

        // Check if config need to use PLAYER_HEAD
        String url = yaml.getString("URL-HEAD");
        // Check Material of this item
        ItemStack buildItem = url.equals("") || url.equals(" ") ? new ItemStack(ItemUtils.materialValueOf(yaml.getString("MATERIAL"), fileName)) : SkullCreator.itemFromUrl(url);

        List<String> listLore = new ArrayList<>();
        ItemMeta buildItemItemMeta = buildItem.getItemMeta();

        // Item lore
        yaml.getStringList("LORE").forEach(loreString -> {
            if (loreString.contains("<owner>") && ownerName != null && ownerName.equals("[[]]")) {  // If <owner> not represent any player
                loreString = loreString.replaceAll("<owner>", ConfigManager.getAdvancedFishYaml().getString("BOOK-PLAYER-NAME"));
                listLore.add(loreString.replaceAll("&", "§"));
            } else if (loreString.contains("<owner>") && ownerName != null) {  // If <owner> represent player
                loreString = loreString.replaceAll("<owner>", ownerName);
                listLore.add(loreString.replaceAll("&", "§"));
            } else {  // No <owner> to replace
                listLore.add(loreString.replaceAll("&", "§"));
            }
        });

        // Set amount
        buildItem.setAmount(yaml.getInt("AMOUNT"));
        // Set durability
        buildItem.setDurability((short) yaml.getInt("DATA"));
        // Set CustomModelData
        buildItem.getItemMeta().setCustomModelData(yaml.getInt("MODEL-DATA"));

        if (buildItemItemMeta != null) {
            buildItemItemMeta.setDisplayName(CC.translate(yaml.getString("NAME")));
            buildItemItemMeta.setLore(listLore);

            // Set enchant
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

    // Item similar check by name and material, as well as lore of them
    public static boolean checkItemStackSame(ItemStack originalItem, ItemStack checkItem) {
        ItemMeta originalItemItemMeta = originalItem.getItemMeta();
        ItemMeta checkItemItemMeta = checkItem.getItemMeta();

        if (originalItemItemMeta == null || checkItemItemMeta == null || checkItemItemMeta.getLore() == null) return false;

        return checkItemItemMeta.getDisplayName().equals(originalItemItemMeta.getDisplayName()) && checkItem.getType() == originalItem.getType();
    }

    // Check if material unknown
    public static Material materialValueOf(String materialString, String fileName) {
        Material material;

        try { material = Material.valueOf(materialString); }
        catch (Exception exception) { CC.sendUnknownWarn("物品", fileName, materialString); return Material.AIR; }

        return material;
    }
}
