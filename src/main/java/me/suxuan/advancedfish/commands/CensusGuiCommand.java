package me.suxuan.advancedfish.commands;

import me.suxuan.advancedfish.fishmatch.FishMatchCensusGui;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;

/**
 * @author: CBer_SuXuan
 * @project: AdvancedFish
 * @className: CensusGuiCommand
 * @date: 2023/4/6 20:38
 * @description: GUI command
 */
public class CensusGuiCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString()
                    + "此指令只有玩家可以使用。");
            return false;
        }

        FishMatchCensusGui.openGui(player);

        return true;
    }
}
