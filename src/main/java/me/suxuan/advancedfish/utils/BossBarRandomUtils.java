package me.suxuan.advancedfish.utils;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

/**
 * @author: CBer_SuXuan
 * @project: AdvancedFish
 * @className: BossBarRandomUtils
 * @date: 2023/4/5 19:46
 * @description: Random Boss Bar color and
 */
public class BossBarRandomUtils {

    // Come from Advanced Exp Booster
    // Written by twomillions -> https://www.mcbbs.net/thread-1397855-1-1.html

    public static BarColor randomColor() {
        int num = (int) (Math.random() * 6);

        switch (num) {
            case 0:
                return BarColor.PINK;
            case 1:
                return BarColor.BLUE;
            case 2:
                return BarColor.RED;
            case 3:
                return BarColor.GREEN;
            case 4:
                return BarColor.YELLOW;
            case 5:
                return BarColor.PURPLE;
            case 6:
                return BarColor.WHITE;
        }

        return BarColor.WHITE;
    }

    public static BarStyle randomStyle() {
        int num = (int) (Math.random() * 4);

        switch (num) {
            case 0:
                return BarStyle.SOLID;
            case 1:
                return BarStyle.SEGMENTED_6;
            case 2:
                return BarStyle.SEGMENTED_10;
            case 3:
                return BarStyle.SEGMENTED_12;
            case 4:
                return BarStyle.SEGMENTED_20;
        }

        return BarStyle.SEGMENTED_20;
    }
}
