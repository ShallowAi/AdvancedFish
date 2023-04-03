package me.twomillions.plugin.advancedfish.commands;

import me.twomillions.plugin.advancedfish.fishmatch.FishMatchCensusGui;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.commands
 * className:      CensusGuiCommand
 * date:    2022/11/9 21:58
 */
public class CensusGuiCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + "此指令只有玩家可以使用。");
            return false;
        }

        Player player = (Player) sender;
        FishMatchCensusGui.openGui(player);

        return true;
    }
}
