package me.twomillions.plugin.advancedfish.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.utils
 * className:      LocationUtils
 * date:    2022/11/5 16:27
 */
public class LocationUtils {
    // 坐标转换 - 鱼域
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
