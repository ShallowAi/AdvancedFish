package me.suxuan.advancedfish.listener;

import me.suxuan.advancedfish.fishmatch.FishMatchManager;
import me.suxuan.advancedfish.fishmatch.FishMatchState;
import me.suxuan.advancedfish.manager.*;
import me.suxuan.advancedfish.runnable.CheckFishingRunnable;
import me.suxuan.advancedfish.runnable.FishMatchRunnable;
import me.suxuan.advancedfish.runnable.UpdateCheckerRunnable;
import me.suxuan.advancedfish.api.GivePlayerAdvancedFishItemEvent;
import me.suxuan.advancedfish.main;
import me.suxuan.advancedfish.manager.*;
import me.suxuan.advancedfish.runnable.AddDiscoverRecipeRunnable;
import me.suxuan.advancedfish.utils.CC;
import me.suxuan.advancedfish.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.event.player.PlayerFishEvent.State.CAUGHT_ENTITY;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.listener
 * className:      PlayerListener
 * date:    2022/10/30 20:55
 */
public class PlayerListener implements Listener {
    private static String playerFishEventFinalFish;
    private static final Plugin plugin = main.getInstance();

    @EventHandler
    public void onPlayerFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Entity caught = event.getCaught();
        PlayerFishEvent.State state = event.getState();

        // CAUGHT_FISH 并不意味着字面上的鱼 如果钓到一个水瓶 状态仍然是 CAUGHT_FISH
        if (state == CAUGHT_ENTITY) return;

        Item item = (Item) caught;
        ItemStack bait = player.getInventory().getItemInOffHand();

        // Check whether player use bait
        // 鱼饵使用检查
        for (ItemStack allBaitItem : BaitManager.getAllBaitItems()) {
            if (!ItemUtils.checkItemStackSame(allBaitItem, bait)) continue;

            String baitName = BaitManager.baitItemToBaitName(bait);
            if (BaitManager.checkBait(baitName, player)) continue;

            event.setCancelled(true);
            BaitManager.getBaitCantUseMessage(baitName).forEach(m -> player.sendMessage(CC.translate(m)));

            return;
        }

        // 使用 switch 来代替 if
        // 拥有较小的性能提升，虽然不明显
        // 旧版本没有 REEL_IN 这个状态，相同的情况下触发的是 FAILED_ATTEMPT
        switch (state) {
            case FISHING -> PlayerManager.setPlayerFishingStats(player, true);
            case REEL_IN, IN_GROUND -> PlayerManager.setPlayerFishingStats(player, false);
            case BITE -> Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                playerFishEventFinalFish = FishManager.getFinalFish(player);
                EffectSendManager.sendEffect(playerFishEventFinalFish, player, "Fish", "ITEM.BITE", null, null);

                // 进行判断，如果这个副手物品是鱼饵，并且此鱼饵对此鱼有加成则扣除
                BaitManager.getAllBaitItems().forEach(allBaitItem -> {
                    if (!ItemUtils.checkItemStackSame(allBaitItem, bait)) return;
                    if (RegisterManager.getFishAndBait().get(playerFishEventFinalFish).contains(BaitManager.baitItemToBaitName(bait)))
                        bait.setAmount(bait.getAmount() - 1);
                });
            });
            case FAILED_ATTEMPT ->
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> EffectSendManager.sendEffect(playerFishEventFinalFish, player, "Fish", "ITEM.FAILED", null, null));
            case CAUGHT_FISH ->
                // CAUGHT_FISH 并不意味着字面上的鱼 如果钓到一个水瓶 状态仍然是 CAUGHT_FISH
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        if (FishManager.getFishCancelDropExp(playerFishEventFinalFish)) event.setExpToDrop(0);

                        ItemStack fishItem = FishManager.getFishItem(playerFishEventFinalFish, player.getName());

                        item.setItemStack(fishItem);
                        PlayerManager.setPlayerFishingStats(player, false);

                        // 检查是否在钓鱼比赛并且是否正在进行，如果是的话那么就不用 ITEM.CAUGHT 而是 FISH-MATCH-CAUGHT
                        if (FishMatchManager.isPlayerInTheFishMatch(player) && FishMatchManager.getFishMatchState() == FishMatchState.START) {
                            // 添加积分
                            EffectSendManager.sendEffect(playerFishEventFinalFish, player, "Fish", "FISH-MATCH-CAUGHT", null, null);
                            FishMatchManager.addFishMatchPlayerIntegral(player, RegisterManager.getFishMatchPlayerIntegral().get(playerFishEventFinalFish));
                        } else
                            EffectSendManager.sendEffect(playerFishEventFinalFish, player, "Fish", "ITEM.CAUGHT", null, null);

                        GivePlayerAdvancedFishItemEvent givePlayerAdvancedFishItemEvent = new GivePlayerAdvancedFishItemEvent(player, playerFishEventFinalFish, fishItem);
                        Bukkit.getPluginManager().callEvent(givePlayerAdvancedFishItemEvent);
                    });
        }
    }

    // 安全措施 - 检查玩家的副手在钓鱼时时候具有物品变化
    // 由于此插件的鱼类判断是当上钩的那一刻，便决定了此鱼的种类，也就是说如果玩家在上钩后光速扔掉扶手物品，则会导致没有扣除鱼饵
    @EventHandler
    public void onPlayerClickOffHandSlot(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof CraftingInventory) || event.getCurrentItem() == null || event.getClickedInventory() == null || !PlayerManager.isPlayerFishing((Player) event.getWhoClicked())) return;
        if (event.getRawSlot() == 45) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSwapHand(PlayerSwapHandItemsEvent event) {
        if (PlayerManager.isPlayerFishing(event.getPlayer())) event.setCancelled(true);
    }

    // 当玩家进来的时候创建一个 Runnable, 并通过 isPlayerFishing 来判断其状态
    // 这是为了防止玩家在钓鱼时切换物品栏或者是扔出去等操作而造成的没有设置正确的状态
    // 由于这可能有太多的意想不到的状况，所以还是 Runnable 更合适，且这不太可能造成服务器滞后
    // 当玩家离线后此循环会停止
    //
    // 这里还有用于判断玩家是否正在钓鱼比赛，如果在的话那么就返回正面消息
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        CheckFishingRunnable.startRunnable(player);
        AddDiscoverRecipeRunnable.startRunnable(player);

        // 及时更新
        if (!UpdateCheckerRunnable.isLatestVersion && player.isOp()) player.sendMessage(CC.translate(
                "&7[&6&lAdvanced Fish&7] &c您看起来在使用过时的 Advanced Fish 版本! 您应该获取更新以防止未知问题出现! 下载链接: https://github.com/xiaoyueyoqwq/AdvancedFish/releases"
        ));

        // 判断玩家是否正在钓鱼比赛中，如果是则发送消息以及告诉玩家其现在的积分
        if (!FishMatchManager.isFishMatchPlayerDataExists(player)) return;

        FishMatchManager.addPlayerToFishMatch(player);
        FishMatchRunnable.startRunnable(player, FishMatchState.PLAYER_REJOIN, null);
    }

    @EventHandler
    public void onPlayerEating(PlayerItemConsumeEvent event) {
        ItemMeta eatItemMeta = event.getItem().getItemMeta();

        if (eatItemMeta == null || !eatItemMeta.hasLore()) return;

        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            ItemStack eatItem = event.getItem();

            for (ItemStack allFishItem : RegisterManager.getAllFishItems()) {
                String fishName = FishManager.fishItemToFishName(eatItem);
                if (!ItemUtils.checkItemStackSame(allFishItem, eatItem) || !FishManager.getFishCanEat(fishName)) continue;

                EffectSendManager.sendEffect(FishManager.fishItemToFishName(eatItem), player, "Fish", "EATING", null, null);
                return;
            }

            for (ItemStack allFurnaceFishItem : RegisterManager.getAllFurnaceItems()) {
                if (!ItemUtils.checkItemStackSame(allFurnaceFishItem, eatItem)) continue;

                EffectSendManager.sendEffect(FishManager.furnaceItemToFishName(eatItem), player, "Fish", "FURNACE-RECIPE", null, null);
                return;
            }

            for (ItemStack allBaitItem : BaitManager.getAllBaitItems()) {
                String baitName = FishManager.fishItemToFishName(eatItem);
                if (!ItemUtils.checkItemStackSame(allBaitItem, eatItem) || !BaitManager.getBaitCanEat(baitName)) continue;

                EffectSendManager.sendEffect(BaitManager.baitItemToBaitName(eatItem), player, "Bait", "EATING", null, null);
                return;
            }
        });
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack eventItem = event.getItem();
        ItemMeta eventItemMeta = eventItem == null ? null : eventItem.getItemMeta();

        if (eventItemMeta == null || eventItem.getType().isEdible() || eventItemMeta.getLore() == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (ItemStack allFishItems : RegisterManager.getAllFishItems()) {
                String fishName = FishManager.fishItemToFishName(eventItem);

                if (!ItemUtils.checkItemStackSame(allFishItems, eventItem) || !FishManager.getFishCanEat(fishName)) continue;

                EffectSendManager.sendEffect(fishName, player, "Fish", "EATING", null, null);
                eventItem.setAmount(eventItem.getAmount() - 1);

                return;
            }

            for (ItemStack allFurnaceItems : RegisterManager.getAllFurnaceItems()) {
                String fishName = FishManager.fishItemToFishName(eventItem);

                if (!ItemUtils.checkItemStackSame(allFurnaceItems, eventItem)) continue;

                EffectSendManager.sendEffect(fishName, player, "Fish", "FURNACE-RECIPE", null, null);
                eventItem.setAmount(eventItem.getAmount() - 1);

                return;
            }

            for (ItemStack allBaitItems : BaitManager.getAllBaitItems()) {
                if (event.getHand() == EquipmentSlot.OFF_HAND) return;

                String baitName = BaitManager.baitItemToBaitName(eventItem);

                if (!ItemUtils.checkItemStackSame(allBaitItems, eventItem) || !BaitManager.getBaitCanEat(baitName)) continue;

                EffectSendManager.sendEffect(baitName, player, "Bait", "EATING", null, null);
                eventItem.setAmount(eventItem.getAmount() - 1);

                return;
            }
        });
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        ItemStack handItem = event.getItemInHand();
        ItemMeta handItemMeta = event.getItemInHand().getItemMeta();

        if (handItemMeta == null || handItemMeta.hasLore()) return;

        for (ItemStack allFishItems : RegisterManager.getAllFishItems()) {
            if (ItemUtils.checkItemStackSame(allFishItems, handItem)) {
                String fishName = FishManager.fishItemToFishName(handItem);
                if (FishManager.getFishCanBuild(fishName)) continue;

                event.setBuild(false);
                event.setCancelled(true);
                return;
            }
        }

        for (ItemStack allFurnaceItems : RegisterManager.getAllFurnaceItems()) {
            if (ItemUtils.checkItemStackSame(allFurnaceItems, handItem)) {
                String furnaceName = FishManager.fishItemToFishName(handItem);
                if (FishManager.getFurnaceCanBuild(furnaceName)) continue;

                event.setBuild(false);
                event.setCancelled(true);
                return;
            }
        }

        for (ItemStack allBaitItems : BaitManager.getAllBaitItems()) {
            if (ItemUtils.checkItemStackSame(allBaitItems, handItem)) {
                String baitName = BaitManager.baitItemToBaitName(handItem);
                if (BaitManager.getBaitCanBuild(baitName)) continue;

                event.setBuild(false);
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerCraft(CraftItemEvent event) {
        // 放弃使用 PrepareItemCraftEvent 预处理合成，预处理会浪费太多的性能
        // 如果玩家以极快的速度点击合成书刷遍历，那么会存在非常严重的性能问题，严重可能导致服务器主线程炸裂
        // 直接使用 CraftItemEvent 监听最后的合成对象进行替换，放弃使用 Map 记录

        Recipe recipe = event.getRecipe();
        HumanEntity player = event.getView().getPlayer();

        ItemStack result = recipe.getResult();
        ItemMeta newMeta = result.getItemMeta();

        if (!(player instanceof Player) || newMeta == null) return;

        List<String> newMetaLore = newMeta.getLore();

        String playerName = player.getName();
        String craftString = BaitManager.getBaitCraftReplaceString();

        // 如果合成出来的物品是没有lore的 那么就不再执行下面的遍历
        if (newMetaLore == null) return;

        List<String> newLore = new ArrayList<>();

        // 遍历操作，从lore中调取对应的参数替换制作者名
        for (ItemStack allBaitItems : BaitManager.getAllBaitItems()) {
            if (!ItemUtils.checkItemStackSame(allBaitItems, result)) return;

            newMetaLore.forEach(nmLore -> {
                if (nmLore.contains(craftString)) nmLore = nmLore.replaceAll(craftString, playerName);
                newLore.add(nmLore);
            });

            newMeta.setLore(newLore);
            result.setItemMeta(newMeta);
            event.getInventory().setResult(result);

            return;
        }
    }

    // 取消在工作台内的点击
    @EventHandler
    public void onPlayerClickCraft(InventoryClickEvent event) {
        ItemStack eventItem = event.getCurrentItem();
        ItemMeta eventItemMeta = eventItem == null ? null : eventItem.getItemMeta();

        if (event.getInventory().getType() != InventoryType.WORKBENCH ||
                event.getRawSlot() == 0 ||
                eventItem == null ||
                eventItem.getType().equals(Material.AIR) ||
                event.getClickedInventory() == null ||
                eventItemMeta == null ||
                eventItemMeta.getLore() == null
        ) return;

        for (ItemStack allFishItems : RegisterManager.getAllFishItems()) {
            if (ItemUtils.checkItemStackSame(allFishItems, eventItem) && !FishManager.getFishCanCraft(FishManager.fishItemToFishName(allFishItems))) {
                event.setCancelled(true);
                return;
            }
        }

        for (ItemStack allFurnaceFishItems : RegisterManager.getAllFurnaceItems()) {
            if (ItemUtils.checkItemStackSame(allFurnaceFishItems, eventItem) && !FishManager.getFurnaceCanCraft(FishManager.furnaceItemToFishName(allFurnaceFishItems))) {
                event.setCancelled(true);
                return;
            }
        }

        for (ItemStack allBaitItems : BaitManager.getAllBaitItems()) {
            if (ItemUtils.checkItemStackSame(allBaitItems, eventItem) && !BaitManager.getBaitCanCraft(BaitManager.baitItemToBaitName(allBaitItems))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerClickFurnace(InventoryClickEvent event) {
        ItemStack eventItem = event.getCurrentItem();

        if (!(event.getInventory() instanceof FurnaceInventory) || event.getRawSlot() == 2 || eventItem == null || eventItem.getType().equals(Material.AIR) || event.getClickedInventory() == null) return;

        for (ItemStack allFishItems : RegisterManager.getAllFishItems()) {
            if (ItemUtils.checkItemStackSame(allFishItems, eventItem) && !FishManager.getFishCanCombustion(FishManager.fishItemToFishName(allFishItems))) {
                event.setCancelled(true);
                return;
            }
        }

        for (ItemStack allFurnaceFishItems : RegisterManager.getAllFurnaceItems()) {
            if (ItemUtils.checkItemStackSame(allFurnaceFishItems, eventItem) && !FishManager.getFurnaceCanCombustion(FishManager.furnaceItemToFishName(allFurnaceFishItems))) {
                event.setCancelled(true);
                return;
            }
        }

        for (ItemStack allBaitItems : BaitManager.getAllBaitItems()) {
            if (ItemUtils.checkItemStackSame(allBaitItems, eventItem) && !BaitManager.getBaitCanCombustion(BaitManager.baitItemToBaitName(allBaitItems))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerClickAnvil(InventoryClickEvent event) {
        ItemStack eventItem = event.getCurrentItem();

        if (!(event.getInventory() instanceof AnvilInventory) || eventItem == null || event.getClickedInventory() == null) return;

        for (ItemStack allFishItems : RegisterManager.getAllFishItems()) {
            if (ItemUtils.checkItemStackSame(allFishItems, eventItem) && !FishManager.getFishCanAnvil(FishManager.fishItemToFishName(allFishItems))) {
                event.setCancelled(true);
                return;
            }
        }

        for (ItemStack allFurnaceFishItems : RegisterManager.getAllFurnaceItems()) {
            if (ItemUtils.checkItemStackSame(allFurnaceFishItems, eventItem) && !FishManager.getFurnaceCanAnvil(FishManager.furnaceItemToFishName(allFurnaceFishItems))) {
                event.setCancelled(true);
                return;
            }
        }

        for (ItemStack allBaitItems : BaitManager.getAllBaitItems()) {
            if (ItemUtils.checkItemStackSame(allBaitItems, eventItem) && !BaitManager.getBaitCanAnvil(BaitManager.baitItemToBaitName(allBaitItems))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerClickEnchant(InventoryClickEvent event) {
        ItemStack eventItem = event.getCurrentItem();

        if (!(event.getInventory() instanceof EnchantingInventory) || eventItem == null || event.getClickedInventory() == null) return;

        for (ItemStack allFishItems : RegisterManager.getAllFishItems()) {
            if (ItemUtils.checkItemStackSame(allFishItems, eventItem) && !FishManager.getFishCanEnchant(FishManager.fishItemToFishName(allFishItems))) {
                event.setCancelled(true);
                return;
            }
        }

        for (ItemStack allFurnaceFishItems : RegisterManager.getAllFurnaceItems()) {
            if (ItemUtils.checkItemStackSame(allFurnaceFishItems, eventItem) && !FishManager.getFurnaceCanEnchant(FishManager.furnaceItemToFishName(allFurnaceFishItems))) {
                event.setCancelled(true);
                return;
            }
        }

        for (ItemStack allBaitItems : BaitManager.getAllBaitItems()) {
            if (ItemUtils.checkItemStackSame(allBaitItems, eventItem) && !BaitManager.getBaitCanEnchant(BaitManager.baitItemToBaitName(allBaitItems))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerClickFishMatchInventory(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        InventoryType type = inventory.getType();
        if (type != InventoryType.CHEST) return;

        if (event.getView().getTitle().equals(CC.translate(ConfigManager.getFishMatchCensusGuiYaml().getString("TITLE")))) event.setCancelled(true);
    }

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        ItemStack eventItem = event.getSource();

        RegisterManager.getAllFishItems().forEach(f -> {
            if (ItemUtils.checkItemStackSame(f, eventItem)) {
                String fishName = FishManager.fishItemToFishName(f);
                if (!FishManager.getFishEnableFurnaceRecipe(fishName)) return;

                ItemStack item = FishManager.fishItemToFurnaceItem(eventItem);

                // 避免null 即需要继承的钓手为空
                // 如果lore 里面没有钓手则永远不应该出现为null的情况
                // 向控制台输出负面消息并让服主寻求帮助
                if (item == null) {
                    Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " + Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() +
                            CC.CHAT_BAR);
                    Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " + Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() +
                            "出现错误! 此问题应出现于烹饪后的钓者继承，目前已忽略此次的烹饪，这不太应该出现，请向开发者反馈此问题并附带此鱼类的配置文件! 拥有问题的鱼类文件名称 -> " + fishName);
                    Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " + Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() +
                            CC.CHAT_BAR);
                    Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " + Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() +
                            "请向开发者寻求帮助! 插件 Mcbbs 链接: -> https://www.mcbbs.net/thread-1393202-1-1.html");
                    Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[Advanced Fish] " + Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() +
                            CC.CHAT_BAR);

                    return;
                }

                event.setResult(item);
            }
        });
    }
}
