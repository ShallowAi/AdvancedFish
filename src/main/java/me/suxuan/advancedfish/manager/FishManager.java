package me.suxuan.advancedfish.manager;

import de.leonhard.storage.Yaml;
import me.suxuan.advancedfish.main;
import me.suxuan.advancedfish.utils.CC;
import me.suxuan.advancedfish.utils.ItemUtils;
import me.suxuan.advancedfish.utils.ProbabilityUntilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: CBer_SuXuan
 * @project: AdvancedFish
 * @className: FishManager
 * @date: 2023/4/5 13:21
 * @description: Manage fish
 */
public class FishManager {
    private static final Plugin plugin = main.getInstance();

    // Check if player can catch fish
    public static boolean canCatchFish(String fishName, Player player) {
        Yaml fish = new Yaml(fishName, plugin.getDataFolder() + "/Fish");

        // Set path prefix
        fish.setPathPrefix("ITEM.CONDITION");

        // Level check
        if (player.getLevel() < fish.getInt("LEVEL")) return false;

        // Items check
        for (String configInventoryHave : fish.getStringList("INVENTORY-HAVE")) {
            if (configInventoryHave == null || configInventoryHave.length() <= 1) continue;

            String[] item = configInventoryHave.toUpperCase(Locale.ROOT).split(";");
            int amount = Integer.parseInt(item[1]);

            int itemAmount = 0;

            // Amount check
            for (ItemStack is : player.getInventory().all(ItemUtils.materialValueOf(item[0], fishName)).values()) {
                if (is != null && is.getType() == ItemUtils.materialValueOf(item[0], fishName)) itemAmount = itemAmount + is.getAmount();
            }

            if (!player.getInventory().contains(ItemUtils.materialValueOf(item[0], fishName)) || itemAmount < amount) return false;
        }

        // Enchant check
        for (String configEnchantHave : fish.getStringList("ROD-HAVE-ENCHANT")) {
            if (configEnchantHave == null || configEnchantHave.length() <= 1) continue;

            String[] enchantment = configEnchantHave.toLowerCase(Locale.ROOT).split(";");
            int level = Integer.parseInt(enchantment[1]);

            ItemStack rod = player.getInventory().getItemInMainHand();
            ItemMeta rodMeta = rod.getItemMeta();

            if (rodMeta == null) return false;

            Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(enchantment[0]));

            if (ench == null) {
                CC.sendUnknownWarn("附魔", fishName, enchantment[0]);
                return false;
            }

            if (!rod.containsEnchantment(ench) || rodMeta.getEnchantLevel(ench) < level) return false;
        }

        // Effect check
        for (String configPotionEffectsHave : fish.getStringList("PLAYER-HAVE-EFFECTS")) {
            if (configPotionEffectsHave == null || configPotionEffectsHave.length() <= 1) continue;

            String[] effect = configPotionEffectsHave.toUpperCase(Locale.ROOT).split(";");

            String effectString = effect[0];
            PotionEffectType effectType = PotionEffectType.getByName(effectString);

            int amplifier = Integer.parseInt(effect[1]);

            if (effectType == null) {
                CC.sendUnknownWarn("药水效果", fishName, effectString);
                return false;
            }

            if (!player.hasPotionEffect(effectType) || player.getPotionEffect(effectType).getAmplifier() < amplifier) return false;
        }

        // Reset path prefix
        fish.setPathPrefix(null);

        // Bait check
        // 这里使用布尔值进行判断，若有一个为真则就判定过
        boolean baitEnabled = false;
        boolean baitCheckDone = false;
        for (String configBaitSpecific : fish.getStringList("BAIT.SPECIFIC")) {
            if (configBaitSpecific == null || configBaitSpecific.length() <= 1) continue;

            baitEnabled = true;
            ItemStack offHandItem = player.getInventory().getItemInOffHand();

            for (ItemStack allBaitItem : BaitManager.getAllBaitItems()) {
                if (!ItemUtils.checkItemStackSame(allBaitItem, offHandItem)) continue;

                String baitName = BaitManager.baitItemToBaitName(offHandItem);

                if (BaitManager.checkBait(baitName, player) && RegisterManager.getFishAndBait().get(fishName).contains(BaitManager.baitItemToBaitName(offHandItem)))
                    baitCheckDone = true;
            }
        }

        // Biome check
        // 这里使用布尔值进行判断，若有一个为真则就判定过
        boolean biomeEnabled = false;
        boolean biomeCheckDone = false;
        for (String configBiomeSpecific : fish.getStringList("BIOME.SPECIFIC")) {
            if (configBiomeSpecific == null || configBiomeSpecific.length() <= 1) continue;

            biomeEnabled = true;
            String[] configBiome = configBiomeSpecific.split(";");

            if (player.getWorld().getBiome(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).toString().equals(configBiome[0]))
                biomeCheckDone = true;
        }

        // Fish area check
        boolean areaEnable = false;
        boolean areaCheckDone = false;
        for (String configAreaSpecific : fish.getStringList("AREA.SPECIFIC")) {
            if (configAreaSpecific == null || configAreaSpecific.length() <= 1) continue;

            areaEnable = true;
            String[] configArea = configAreaSpecific.split(";");
            String areaName = AreaManager.getPlayerAreaName(player);

            if (areaName != null && areaName.contains(configArea[0])) areaCheckDone = true;
        }

        boolean baitRigid = false;
        boolean biomeRigid = false;
        boolean areaRigid = false;

        // 同时满足检查
        for (String string : fish.getStringList("BBA")) {
            if (string == null || string.length() <= 1) continue;

            // 注意强制大写
            // 这里我写的时候写了强制大写 contains 内写的全小写，找了半个多小时才找到问题在哪
            if (string.toUpperCase(Locale.ROOT).contains("BAIT")) baitRigid = true;
            if (string.toUpperCase(Locale.ROOT).contains("BIOME")) biomeRigid = true;
            if (string.toUpperCase(Locale.ROOT).contains("AREA")) areaRigid = true;
        }

        // 真复杂，写的时候给我整不会了
        if (baitRigid || biomeRigid || areaRigid) {
            if (baitRigid && biomeRigid && areaRigid) {
                if (baitEnabled && !baitCheckDone || biomeEnabled && !biomeCheckDone || areaEnable && !areaCheckDone) return false;
            }
            if (baitRigid && biomeRigid) {
                if (baitEnabled && !baitCheckDone || biomeEnabled && !biomeCheckDone) return false;
            }
            if (baitRigid && areaRigid) {
                if (baitEnabled && !baitCheckDone || areaEnable && !areaCheckDone) return false;
            }
            if (biomeRigid && areaRigid) {
                return (!biomeEnabled || biomeCheckDone) && (!areaEnable || areaCheckDone);
            }
        } else {
            if (biomeEnabled || baitEnabled || areaEnable) return baitCheckDone || biomeCheckDone || areaCheckDone;
        }

        return true;
    }

    // Check all registered fish, and return all fishes that player can catch
    public static List<String> checkAllFish(Player player) {
        List<String> fishList = new ArrayList<>();

        RegisterManager.getRegisterFish().forEach(fish -> {
            if (canCatchFish(fish, player)) fishList.add(fish);
        });

        return fishList;
    }

    // Get final catch fish
    public static String getFinalFish(Player player) {
        ProbabilityUntilities probabilities = new ProbabilityUntilities();

        Location location = player.getLocation();
        World world = player.getWorld();

        ItemStack bait = player.getInventory().getItemInOffHand();
        AtomicBoolean noBait = new AtomicBoolean(false);

        // Check bait and
        BaitManager.getAllBaitItems().forEach(b -> {
            if (ItemUtils.checkItemStackSame(b, bait)) noBait.set(true);
        });

        // 虽然 getBiome(x, z) 已被高版本弃用，但是在 1.16 内无法使用 getBiome(location)
        String baitName = bait.getType().equals(Material.AIR) || !noBait.get() ? null : BaitManager.baitItemToBaitName(bait);
        String biomeName = world.getBiome(location.getBlockX(), location.getBlockZ()).toString();

        checkAllFish(player).forEach(fish -> probabilities.addChance(fish,
                FishManager.getFishProbability(fish, baitName, biomeName, AreaManager.getPlayerAreaName(player))));

        return probabilities.getRandomElement().toString();
    }

    // Get fish ItemStack
    public static ItemStack getFishItem(String fishName, String fishOwnerName) {
        return ItemUtils.buildItemStack(fishName, fishOwnerName, "Fish", "ITEM");
    }

    // Get fish furnace ItemStack
    public static ItemStack getFurnaceItem(String fishName) {
        return ItemUtils.buildItemStack(fishName, "<owner>", "Fish", "FURNACE-RECIPE.ITEM");
    }

    // Fish Item Stack to Furnace Item Stack
    // Owner inherit, find
    public static ItemStack fishItemToFurnaceItem(ItemStack fishItem) {
        ItemMeta fishItemMeta = fishItem.getItemMeta();

        if (fishItemMeta == null || fishItemMeta.getLore() == null) return null;

        List<String> newLore = new ArrayList<>();
        String fishOwnerName = FishManager.contrastAndInheritance(fishItem);
        ItemStack furnaceItem = FishManager.getFurnaceItem(FishManager.fishItemToFishName(fishItem));
        ItemMeta furnaceItemMeta = furnaceItem.getItemMeta();

        if (furnaceItemMeta == null) return null;

        if (furnaceItemMeta.getLore() != null) {
            furnaceItemMeta.getLore().forEach(lore -> {
                if (lore.contains("<owner>") && fishOwnerName != null) lore = lore.replaceAll("<owner>", fishOwnerName);
                newLore.add(lore.replaceAll("&", "§"));
            });
        }

        furnaceItemMeta.setLore(newLore);
        furnaceItem.setItemMeta(furnaceItemMeta);

        return furnaceItem;
    }

    // Owner inherit, direct
    public static ItemStack fishItemToFurnaceItem(ItemStack fishItem, String setFishOwnerName) {
        ItemMeta fishItemMeta = fishItem.getItemMeta();

        if (fishItemMeta == null || fishItemMeta.getLore() == null) return null;

        List<String> newLore = new ArrayList<>();
        ItemStack furnaceItem = FishManager.getFurnaceItem(FishManager.fishItemToFishName(fishItem));
        ItemMeta furnaceItemMeta = furnaceItem.getItemMeta();

        if (furnaceItemMeta == null) return null;

        if (furnaceItemMeta.getLore() != null) {
            furnaceItemMeta.getLore().forEach(lore -> {
                if (lore.contains("<owner>") && setFishOwnerName != null) lore = lore.replaceAll("<owner>", setFishOwnerName);
                newLore.add(lore.replaceAll("&", "§"));
            });
        }

        furnaceItemMeta.setLore(newLore);
        furnaceItem.setItemMeta(furnaceItemMeta);

        return furnaceItem;
    }

    // get fishName from ItemStack with map in RegisterManager
    public static String fishItemToFishName(ItemStack fishItem) {
        return RegisterManager.getItemAndFish().get(fishItem.getItemMeta().getDisplayName());
    }

    // get fishName from furnace with map in RegisterManager
    public static String furnaceItemToFishName(ItemStack fishItem) {
        return RegisterManager.getFurnaceItemAndFish().get(fishItem.getItemMeta().getDisplayName());
    }

    // 尝试查找位置，并进行对比，以获得钓手名
    public static String contrastAndInheritance(ItemStack i) {
        ItemMeta iMeta = i.getItemMeta();

        if (iMeta == null || iMeta.getLore() == null) return null;

        ItemStack rItem = FishManager.getFishItem(FishManager.fishItemToFishName(i), "[]");
        ItemMeta rItemMeta = rItem.getItemMeta();

        if (rItemMeta == null) return null;

        List<String> iLore = iMeta.getLore();
        List<String> rLore = rItemMeta.getLore();

        if (rLore == null) return null;

        int j = 0;
        int index = 0;
        int line = 0;

        String playerName = "";

        // 通过遍历与对比获取钓手所在的行号与字符号
        for (String rL : rLore) {
            if (line > rLore.size()) break;

            if (!rL.contains("[]")) {
                line ++;
                continue;
            }

            index = rL.indexOf("[]");

            break;
        }

        // 通过物品以及上面所获取的行号字符号获取所对应的钓手名
        for (String s : iLore) {
            if (j != line) {
                j ++;
                continue;
            }

            playerName = s.substring(index);
            break;
        }

        return playerName;
    }

    // Probability of get fish
    public static int getFishProbability(String fishName, String baitName, String biomeName, String areaName) {
        Yaml fish = new Yaml(fishName, plugin.getDataFolder() + "/Fish");

        // Base probability
        int baseGain = fish.getInt("ITEM.PROBABILITY");

        AtomicInteger baitGain = new AtomicInteger();
        AtomicInteger biomeGain = new AtomicInteger();
        AtomicInteger areaGain = new AtomicInteger();

        // Traversal bait probability
        fish.getStringList("BAIT.SPECIFIC").forEach(s -> {
            if (baitName == null || s == null || s.length() <= 1) return;
            if (!s.contains(baitName)) return;

            String[] getBait = s.split(";");

            baitGain.set(Integer.parseInt(getBait[1]));
        });

        fish.getStringList("BAIT.AVAILABLE").forEach(a -> {
            if (baitName == null || a == null || a.length() <= 1) return;
            if (!a.contains(baitName)) return;

            String[] getBait = a.split(";");

            baitGain.set(Integer.parseInt(getBait[1]));
        });

        // Traversal biome probability
        fish.getStringList("BIOME.SPECIFIC").forEach(s -> {
            if (biomeName == null || s == null || s.length() <= 1) return;
            if (!s.contains(biomeName)) return;

            String[] getBiome = s.split(";");

            biomeGain.set(Integer.parseInt(getBiome[1]));
        });

        fish.getStringList("BIOME.AVAILABLE").forEach(s -> {
            if (biomeName == null || s == null || s.length() <= 1) return;
            if (!s.contains(biomeName)) return;

            String[] getBiome = s.split(";");

            biomeGain.set(Integer.parseInt(getBiome[1]));
        });

        // Traversal fish area probability
        fish.getStringList("AREA.SPECIFIC").forEach(a -> {
            if (areaName == null || a == null || a.length() <= 1) return;
            if (!a.contains(areaName)) return;

            String[] getArea = a.split(";");

            areaGain.set(Integer.parseInt(getArea[1]));
        });

        fish.getStringList("AREA.AVAILABLE").forEach(a -> {
            if (areaName == null || a == null || a.length() <= 1) return;
            if (!a.contains(areaName)) return;

            String[] getArea = a.split(";");

            areaGain.set(Integer.parseInt(getArea[1]));
        });

        return baseGain + baitGain.get() + biomeGain.get() + areaGain.get();
    }

    // Can eat?
    public static boolean getFishCanEat(String fishName) {
        return RegisterManager.getFishEatList().contains(fishName);
    }

    // Cancel EXP?
    public static boolean getFishCancelDropExp(String fishName) {
        return RegisterManager.getFishCancelDropExpList().contains(fishName);
    }

    // Can build?
    public static boolean getFishCanBuild(String fishName) {
        return RegisterManager.getFishBuildList().contains(fishName);
    }

    // Can craft?
    public static boolean getFishCanCraft(String fishName) {
        return RegisterManager.getFishCraftList().contains(fishName);
    }

    // 此Can combustion?
    public static boolean getFishCanCombustion(String fishName) {
        return RegisterManager.getFishCombustionList().contains(fishName);
    }

    // Can anvil?
    public static boolean getFishCanAnvil(String fishName) {
        return RegisterManager.getFishAnvilList().contains(fishName);
    }

    // Can enchant?
    public static boolean getFishCanEnchant(String fishName) {
        return RegisterManager.getFishEnchantList().contains(fishName);
    }

    // Custom recipe?
    public static boolean getFishEnableFurnaceRecipe(String fishName) {
        return RegisterManager.getFishFurnaceRecipeList().contains(fishName);
    }

    // Cooked fish

    // Can build?
    public static boolean getFurnaceCanBuild(String fishName) {
        return RegisterManager.getFurnaceBuildList().contains(fishName);
    }

    // Can craft?
    public static boolean getFurnaceCanCraft(String fishName) {
        return RegisterManager.getFurnaceCraftList().contains(fishName);
    }

    // Can combustion?
    public static boolean getFurnaceCanCombustion(String fishName) {
        return RegisterManager.getFurnaceCombustionList().contains(fishName);
    }

    // Can anvil?
    public static boolean getFurnaceCanAnvil(String fishName) {
        return RegisterManager.getFurnaceAnvilList().contains(fishName);
    }

    // Can enchant?
    public static boolean getFurnaceCanEnchant(String fishName) {
        return RegisterManager.getFurnaceEnchantList().contains(fishName);
    }
}

