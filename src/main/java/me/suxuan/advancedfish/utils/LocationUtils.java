package me.suxuan.advancedfish.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author: CBer_SuXuan
 * @project: AdvancedFish
 * @className: LocationUtils
 * @date: 2023/4/5 19:42
 * @description: Swap Coordinates and LocationString
 */
public class LocationUtils {
    // Fish area coordinate transformation
    public static String locationToString(final Location location) {
        if (location == null) return "";
        return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ();
    }

    public static Location stringToLocation(final String stringLocation) {
        final String[] parts = stringLocation.split(";");
        if (parts.length != 4) return null;

        final World w = Bukkit.getServer().getWorld(parts[0]);
        final double x = Double.parseDouble(parts[1]);
        final double y = Double.parseDouble(parts[2]);
        final double z = Double.parseDouble(parts[3]);
        return new Location(w, x, y, z);
    }
}
