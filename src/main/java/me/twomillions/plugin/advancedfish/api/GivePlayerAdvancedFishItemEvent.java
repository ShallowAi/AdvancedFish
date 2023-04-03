package me.twomillions.plugin.advancedfish.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * author:     2000000
 * project:    CustomFish
 * package:        me.twomillions.plugin.advancedfish.api
 * className:      GivePlayerAdvancedFishItemEvent
 * date:    2022/10/31 16:18
 */
public class GivePlayerAdvancedFishItemEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;

    private final String fishName;

    private final ItemStack fishItem;

    public GivePlayerAdvancedFishItemEvent(Player player, String fishName, ItemStack fishItem) {
        super(!Bukkit.isPrimaryThread());

        this.player = player;
        this.fishName = fishName;
        this.fishItem = fishItem;
    }

    public Player getPlayer() {
        return player;
    }

    public String getFishName() {
        return fishName;
    }

    public ItemStack getFishItem() {
        return fishItem;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
