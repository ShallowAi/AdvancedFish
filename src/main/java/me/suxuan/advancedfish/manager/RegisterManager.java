package me.suxuan.advancedfish.manager;

import de.leonhard.storage.Yaml;
import lombok.Getter;
import me.suxuan.advancedfish.commands.*;
import me.suxuan.advancedfish.listener.PlayerListener;
import me.suxuan.advancedfish.main;
import me.suxuan.advancedfish.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @author: CBer_SuXuan
 * @project: AdvancedFish
 * @className: RegisterManager
 * @date: 2023/4/5 12:33
 * @description: Manage Register, include Fish, Bait, Listener, Commands
 */
public class RegisterManager {
    private static final Plugin plugin = main.getInstance();

    // Use container operation, avoid getting stuck by reading configuration files on site
    @Getter
    private static final List<String> registerFish = new ArrayList<>();
    @Getter private static final List<String> registerBait = new ArrayList<>();
    @Getter private static final List<NamespacedKey> registerRecipe = new ArrayList<>();

    @Getter private static final List<ItemStack> allFishItems = new ArrayList<>();
    @Getter private static final List<ItemStack> allBaitItems = new ArrayList<>();
    @Getter private static final List<ItemStack> allFurnaceItems = new ArrayList<>();

    @Getter private static final Map<String, String> itemAndFish = new ConcurrentHashMap<>();
    @Getter private static final Map<String, String> furnaceItemAndFish = new ConcurrentHashMap<>();

    @Getter private static final Map<String, String> itemAndBait = new ConcurrentHashMap<>();
    @Getter private static final Map<String, List<String>> fishAndBait = new ConcurrentHashMap<>();

    // Fish use limit
    @Getter private static final List<String> fishEatList = new ArrayList<>();
    @Getter private static final List<String> fishBuildList = new ArrayList<>();
    @Getter private static final List<String> fishCraftList = new ArrayList<>();
    @Getter private static final List<String> fishAnvilList = new ArrayList<>();
    @Getter private static final List<String> fishEnchantList = new ArrayList<>();
    @Getter private static final List<String> fishCombustionList = new ArrayList<>();
    @Getter private static final List<String> fishCancelDropExpList = new ArrayList<>();
    @Getter private static final List<String> fishFurnaceRecipeList = new ArrayList<>();

    // Fish cooking use limit
    @Getter private static final List<String> furnaceBuildList = new ArrayList<>();
    @Getter private static final List<String> furnaceCraftList = new ArrayList<>();
    @Getter private static final List<String> furnaceCombustionList = new ArrayList<>();
    @Getter private static final List<String> furnaceAnvilList = new ArrayList<>();
    @Getter private static final List<String> furnaceEnchantList = new ArrayList<>();

    // Bait use limit
    @Getter private static final List<String> baitEatList = new ArrayList<>();
    @Getter private static final List<String> baitBuildList = new ArrayList<>();
    @Getter private static final List<String> baitCraftList = new ArrayList<>();
    @Getter private static final List<String> baitAnvilList = new ArrayList<>();
    @Getter private static final List<String> baitEnchantList = new ArrayList<>();
    @Getter private static final List<String> baitCombustionList = new ArrayList<>();

    // Fish match score
    @Getter private static final Map<String, Double> fishMatchPlayerIntegral = new ConcurrentHashMap<>();

    // Register all fish
    public static void registerAllFish() {
        // clear
        registerFish.clear();
        itemAndFish.clear();
        furnaceItemAndFish.clear();
        allFishItems.clear();

        fishEatList.clear();
        fishCancelDropExpList.clear();
        fishBuildList.clear();
        fishCraftList.clear();
        fishCombustionList.clear();
        fishAnvilList.clear();
        fishEnchantList.clear();
        fishFurnaceRecipeList.clear();
        fishMatchPlayerIntegral.clear();

        furnaceBuildList.clear();
        furnaceCraftList.clear();
        furnaceCombustionList.clear();
        furnaceAnvilList.clear();
        furnaceEnchantList.clear();

        // register
        for (String fishName : ConfigManager.getAdvancedFishYaml().getStringList("FISH")) {
            if (fishName == null || fishName.equals("") || fishName.equals(" ")) return;

            ConfigManager.createYamlConfig(fishName, "/Fish", true);
            Yaml fishYaml = new Yaml(fishName, plugin.getDataFolder() + "/Fish");
            ItemStack fishItem = FishManager.getFishItem(fishName, null);

            if (fishItem.getItemMeta() == null) return;

            // write in
            registerFish.add(fishName);
            allFishItems.add(fishItem);

            itemAndFish.put(fishItem.getItemMeta().getDisplayName(), fishName);

            // write config
            if (fishYaml.getBoolean("EATING.CAN-EAT")) fishEatList.add(fishName);  // Can eat?
            if (fishYaml.getBoolean("ITEM.CAUGHT.REMOVE-DROP-EXP")) fishCancelDropExpList.add(fishName);  // Can drop EXP?

            fishYaml.setPathPrefix("USE");

            // Can player use this fish
            if (fishYaml.getBoolean("CAN-BUILD")) fishBuildList.add(fishName);  // Can build?
            if (fishYaml.getBoolean("CAN-CRAFT")) fishCraftList.add(fishName);  // Can craft?
            if (fishYaml.getBoolean("CAN-COMBUSTION")) fishCombustionList.add(fishName);  // Can combustion?
            if (fishYaml.getBoolean("CAN-ANVIL")) fishAnvilList.add(fishName);  // Can anvil?
            if (fishYaml.getBoolean("CAN-ENCHANT")) fishEnchantList.add(fishName);  // Can enchant?

            // Match score
            fishYaml.setPathPrefix(null);
            if (fishYaml.getDouble("FISH-MATCH") != 0) fishMatchPlayerIntegral.put(fishName, fishYaml.getDouble("FISH-MATCH"));

            // Can player use furnace, only effect on fish
            if (fishYaml.getBoolean("FURNACE-RECIPE.ENABLE")) {
                // after cooking item
                ItemStack fishFurnaceItem = FishManager.getFurnaceItem(fishName);

                if (fishFurnaceItem.getItemMeta() == null) return;

                fishFurnaceRecipeList.add(fishName);
                allFurnaceItems.add(fishFurnaceItem);
                furnaceItemAndFish.put(fishFurnaceItem.getItemMeta().getDisplayName(), fishName);

                // cooking thing config write
                fishYaml.setPathPrefix("USE-FURNACE");

                if (fishYaml.getBoolean("CAN-BUILD")) furnaceBuildList.add(fishName);
                if (fishYaml.getBoolean("CAN-CRAFT")) furnaceCraftList.add(fishName);
                if (fishYaml.getBoolean("CAN-COMBUSTION")) furnaceCombustionList.add(fishName);
                if (fishYaml.getBoolean("CAN-ANVIL")) furnaceAnvilList.add(fishName);
                if (fishYaml.getBoolean("CAN-ENCHANT")) furnaceEnchantList.add(fishName);
            }

            Bukkit.getLogger().info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                    Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + "已成功加载鱼类并设置其与物品名称的关系! 鱼类文件名称 " +
                    Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString() + "-> " +
                    Ansi.ansi().fg(Ansi.Color.BLUE).boldOff().toString() + fishName);
        }
    }

    // Register all bait, include recipe
    public static void registerAllBait() {
        // clear
        registerBait.clear();
        registerRecipe.clear();

        itemAndBait.clear();
        allBaitItems.clear();

        baitEatList.clear();
        baitAnvilList.clear();
        baitBuildList.clear();
        baitCraftList.clear();
        baitEnchantList.clear();
        baitCombustionList.clear();

        // Register normal bait
        for (String baitName : ConfigManager.getAdvancedFishYaml().getStringList("BAIT")) {
            if (baitName == null || baitName.equals("") || baitName.equals(" ")) return;

            ConfigManager.createYamlConfig(baitName, "/Bait", true);
            ItemStack baitItem = BaitManager.getBaitItem(baitName, null);
            Yaml baitYaml = new Yaml(baitName, plugin.getDataFolder() + "/Bait");

            if (baitItem.getItemMeta() == null) return;

            registerBait.add(baitName);
            allBaitItems.add(baitItem);
            itemAndBait.put(baitItem.getItemMeta().getDisplayName(), baitName);

            if (baitYaml.getBoolean("EATING.CAN-EAT")) baitEatList.add(baitName);

            baitYaml.setPathPrefix("USE");

            if (baitYaml.getBoolean("CAN-BUILD")) baitBuildList.add(baitName);
            if (baitYaml.getBoolean("CAN-CRAFT")) baitCraftList.add(baitName);
            if (baitYaml.getBoolean("CAN-COMBUSTION")) baitCombustionList.add(baitName);
            if (baitYaml.getBoolean("CAN-ANVIL")) baitAnvilList.add(baitName);
            if (baitYaml.getBoolean("CAN-ENCHANT")) baitEnchantList.add(baitName);

            Bukkit.getLogger().info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                    Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() +  "已成功加载鱼饵并将其写入容器! 鱼饵文件名称 " +
                    Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString() + "-> " +
                    Ansi.ansi().fg(Ansi.Color.BLUE).boldOff().toString() + baitName);
        }

        // Write limit
        for (String registerFish : RegisterManager.getRegisterFish()) {
            List<String> list = new ArrayList<>();

            Yaml fishYaml = new Yaml(registerFish, plugin.getDataFolder() + "/Fish");

            // SPECIFIC
            for (String configBaitSpecific : fishYaml.getStringList("BAIT.SPECIFIC")) {
                if (configBaitSpecific == null || configBaitSpecific.length() <= 1) continue;
                // depart
                String[] configBaitSpecificList = configBaitSpecific.split(";");
                String baitName = configBaitSpecificList[0];

                if (RegisterManager.getRegisterBait().contains(baitName)) {
                    if (!list.contains(baitName)) {
                        list.add(baitName);
                        Bukkit.getLogger().info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                                Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + "已成功加载鱼类与鱼饵的使用关系! 鱼类文件名称/鱼饵文件名称 " +
                                Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString() + "-> " +
                                Ansi.ansi().fg(Ansi.Color.BLUE).boldOff().toString() + registerFish + "/" + baitName);
                    }
                } else {
                    Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                            Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "加载错误! 检查到您在鱼类内添加了一个未知的鱼饵，请检查此鱼类的配置文件! 鱼类文件名称/未知鱼饵名 " +
                            Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString() + "-> " +
                            Ansi.ansi().fg(Ansi.Color.BLUE).boldOff().toString() + registerFish + "/" + baitName);
                }
            }

            // AVAILABLE
            for (String configAvailableSpecific : fishYaml.getStringList("BAIT.AVAILABLE")) {
                if (configAvailableSpecific == null || configAvailableSpecific.length() <= 1) continue;
                String[] configAvailableSpecificList = configAvailableSpecific.split(";");
                String baitName = configAvailableSpecificList[0];

                if (RegisterManager.getRegisterBait().contains(baitName)) {
                    if (!list.contains(baitName)) {
                        list.add(baitName);
                        Bukkit.getLogger().info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                                Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + "已成功加载鱼类与鱼饵的使用关系! 鱼类文件名称/鱼饵文件名称 " +
                                Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString() + "-> " +
                                Ansi.ansi().fg(Ansi.Color.BLUE).boldOff().toString() + registerFish + "/" + baitName);
                    }
                } else {
                    Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                            Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() +
                            "加载错误! 检查到您在鱼类内添加了一个未知的鱼饵，请检查此鱼类的配置文件! 鱼类文件名称/未知鱼饵名 " +
                            Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString() +
                            "-> " +
                            Ansi.ansi().fg(Ansi.Color.BLUE).boldOff().toString() +
                            registerFish + "/" + baitName);
                }
            }

            RegisterManager.fishAndBait.put(registerFish, list);
        }

        // Register recipe
        for (String registerBait : RegisterManager.getRegisterBait()) {
            Yaml bait = new Yaml(registerBait, plugin.getDataFolder() + "/Bait");
            bait.setPathPrefix("CUSTOM-FORMULA");

            if (!bait.getBoolean("ENABLE")) return;

            ItemStack item = BaitManager.getBaitItem(registerBait, "[[]]");

            NamespacedKey nKey = new NamespacedKey(plugin, registerBait);
            ShapedRecipe shapedRecipe = new ShapedRecipe(nKey, item);

            shapedRecipe.shape(
                    bait.getString("LINE1"),
                    bait.getString("LINE2"),
                    bait.getString("LINE3")
            );

            bait.getStringList("ITEMS").forEach(configItem -> {
                String[] configItemSplit = configItem.split(";");
                String code = configItemSplit[0];
                char codeChar = code.toCharArray()[0];
                Material material = ItemUtils.materialValueOf(configItemSplit[1], registerBait);

                shapedRecipe.setIngredient(codeChar, material);
            });

            try { Bukkit.addRecipe(shapedRecipe); }
            catch (Exception exception) {
                Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                                Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "注册鱼饵配方 " +
                                Ansi.ansi().fg(Ansi.Color.BLUE).boldOff().toString() + registerBait +
                                Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + " 失败，请检查配置文件或者重新加载！ "
                        );
                return;
            }

            registerRecipe.add(nKey);

            Bukkit.getLogger().info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " +
                    Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + "已成功注册鱼饵的合成配方! 鱼饵文件名称 " +
                    Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString() + "-> " +
                    Ansi.ansi().fg(Ansi.Color.BLUE).boldOff().toString() + registerBait);
        }
    }

    // Register Commands
    public static void registerCommands() {
        Objects.requireNonNull(main.getInstance().getCommand("advancedfish")).setExecutor(new MainCommand());
        Objects.requireNonNull(main.getInstance().getCommand("advancedfish")).setTabCompleter(new MainCommand());
        Objects.requireNonNull(main.getInstance().getCommand("getenchantment")).setExecutor(new EnchantmentCommand());
        Objects.requireNonNull(main.getInstance().getCommand("getpotioneffect")).setExecutor(new PotionEffectCommand());
        Objects.requireNonNull(main.getInstance().getCommand("getmaterial")).setExecutor(new MaterialCommand());
        Objects.requireNonNull(main.getInstance().getCommand("area")).setExecutor(new AreaCommand());
        Objects.requireNonNull(main.getInstance().getCommand("getbiome")).setExecutor(new BiomeCommand());
        Objects.requireNonNull(main.getInstance().getCommand("fishmatch")).setExecutor(new FishMatchCommand());
        Objects.requireNonNull(main.getInstance().getCommand("advancedconsole")).setExecutor(new ConsoleCommand());
        Objects.requireNonNull(main.getInstance().getCommand("census")).setExecutor(new CensusGuiCommand());
    }

    // Register Listener
    public static void registerListener() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
    }
}