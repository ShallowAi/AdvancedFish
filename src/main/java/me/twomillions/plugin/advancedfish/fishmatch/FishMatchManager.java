package me.twomillions.plugin.advancedfish.fishmatch;

import de.leonhard.storage.Json;
import lombok.Getter;
import lombok.Setter;
import me.twomillions.plugin.advancedfish.main;
import me.twomillions.plugin.advancedfish.manager.ConfigManager;
import me.twomillions.plugin.advancedfish.runnable.FishMatchRunnable;
import me.twomillions.plugin.advancedfish.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.fishmatch
 * className:      FishMatchManager
 * date:    2022/11/9 9:59
 */
public class FishMatchManager {
    private static final Plugin plugin = main.getInstance();

    // 记录进入钓鱼比赛的玩家
    @Getter private static final List<Player> fishMatchPlayers = new ArrayList<>();

    // 比赛状态
    @Getter @Setter private static FishMatchState fishMatchState = FishMatchState.NONE;

    // Map 记录最终
    @Getter
    public static Map<String, Double> playerFishMatchIntegral = new ConcurrentHashMap<>();

    // 检查玩家的 Json 数据文件是否存在
    public static boolean isFishMatchPlayerDataExists(Player player) {
        File json = new File(plugin.getDataFolder() + "/FishMatchPlayerData", player.getName() + ".json");
        return json.exists();
    }

    // 使用 Json 查找钓鱼比赛数据的积分
    public static Double getJsonFishMatchPlayerIntegral(Player player) {
        String name = player.getName();

        // 这里先检查文件是否存在是为了防止玩家不在钓鱼的时候却添加了 Json 文件
        // 这个方法是查询文件数据积分，文件没有的话返回的就是 0.0 不会有任何影响
        if (!FishMatchManager.isFishMatchPlayerDataExists(player)) return 0.0;

        Json json = new Json(name, plugin.getDataFolder() + "/FishMatchPlayerData");

        return json.getDouble("INTEGRAL");
    }

    // 添加玩家积分
    public static void addFishMatchPlayerIntegral(Player player, double integral) {
        String name = player.getName();

        Json json = new Json(name, plugin.getDataFolder() + "/FishMatchPlayerData");

        json.set("INTEGRAL", json.getDouble("INTEGRAL") + integral);
    }

    // 设置玩家积分
    public static void setFishMatchPlayerIntegral(Player player, double integral) {
        String name = player.getName();

        Json json = new Json(name, plugin.getDataFolder() + "/FishMatchPlayerData");

        json.set("INTEGRAL", integral);
    }

    // 添加玩家加入比赛
    public static void addPlayerToFishMatch(Player player) {
        if (isPlayerInTheFishMatch(player)) return;

        fishMatchPlayers.add(player);
    }

    // 删除已加入钓鱼比赛的玩家
    public static void removePlayerAtFishMatch(Player player) {
        if (!isPlayerInTheFishMatch(player)) return;

        fishMatchPlayers.remove(player);
    }

    // 检查玩家是否在钓鱼比赛内
    public static boolean isPlayerInTheFishMatch(Player player) {
        return fishMatchPlayers.contains(player);
    }

    // 删除玩家 Json 临时文件
    public static void deleteFishMatchPlayerDataDirectory() {
        File file = new File(plugin.getDataFolder() + "/FishMatchPlayerData");
        deleteFile(file);
    }

    // 遍历删除文件夹及其子文件
    private static void deleteFile(File file) {
        if (file == null || !file.exists()) return;

        File[] files = file.listFiles();
        if (files == null) return;

        for (File f : files) f.delete();
    }

    // 创建一个比赛
    public static void createFishMatch(Player player) {
        // 如果比赛正在冷却
        if (getFishMatchState() == FishMatchState.START_COUNTDOWN) {
            ConfigManager.getMessageYaml().getStringList("START-COOLDOWN").forEach(m -> player.sendMessage(CC.translate(m)));
            return;
        }

        // 如果有比赛正在进行
        if (getFishMatchState() != FishMatchState.NONE) {
            ConfigManager.getMessageYaml().getStringList("MATCH-HAS-BEEN-START").forEach(m -> player.sendMessage(CC.translate(m)));
            return;
        }

        // 如果人数不足
        if (Bukkit.getOnlinePlayers().size() < ConfigManager.getFishMatchYaml().getInt("MIN-PLAYER")) {
            FishMatchRunnable.startRunnable(player, FishMatchState.INSUFFICIENT, null);
            return;
        }

        // 开启等待
        // 这里创建多个计划任务倒计时
        FishMatchRunnable.startRunnable(player, FishMatchState.HOLD, null);
        FishMatchRunnable.startCountDownRunnable(player);

        // 倒计时完毕后开启比赛
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            // 人数判断
            if (getFishMatchPlayers().size() < ConfigManager.getFishMatchYaml().getInt("MIN-PLAYER")) {
                FishMatchRunnable.startRunnable(player, FishMatchState.INSUFFICIENT, null);
                return;
            }

            // 开始比赛
            FishMatchRunnable.startRunnable(player, FishMatchState.START, null);
        }, 60 * 20);

        // 接下来创建多个计划任务，制作结束倒计时
        // time * 60L * 20 (时间) - 10 * 20 (往前推10秒进行倒计时) + 60 * 20 (开始的60秒)
        int time = ConfigManager.getFishMatchYaml().getInt("TIME");
        FishMatchRunnable.endedCountDownRunnable(player, time);

        // 倒计时完毕后设置状态为结束
        // Runnable 内结束后会自动设置冷却 设置状态为 START_COUNTDOWN
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> FishMatchRunnable.startRunnable(player, FishMatchState.ENDED, null), time * 60L * 20 + 60 * 20);

        // 钓鱼比赛冷却时间
        int countDown = ConfigManager.getFishMatchYaml().getInt("COUNT-DOWN");
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> Bukkit.getScheduler().runTask(plugin, () -> setFishMatchState(FishMatchState.NONE)), time * 60L * 20 + 60 * 20 + countDown * 60L);
    }

    // 获取排名 使用 Map 分割 ','
    // 内容: {a=20.0, b=15.0, c=12.0, d=10.0, Real2000000=7.0}
    // 简单暴力获取内容
    public static Integer playerNameGetCensus(String key) {
        int size = playerFishMatchIntegral.size();

        String[] map = playerFishMatchIntegral.toString().split(",");
        Integer census = null;

        for (int i = 0; i < size; i++) {
            if (!map[i].contains(key)) continue;

            census = i;
            break;
        }

        return census == null ? null : census + 1;
    }

    // 暴力获取，这看起来并不会造成效率问题，非常的简单
    // 内容: {a=20.0, b=15.0, c=12.0, d=10.0, Real2000000=7.0}
    // 获取到分割 ',' 后的具体排名后，只需要对 '=' 进行分割，获得 x=0.0 直接0 获取到对象
    public static String censusGetPlayer(int census) {
        census = census - 1;

        String[] map = playerFishMatchIntegral.toString().split(",");
        String[] line;

        try {
            line = map[census].split("=");
            // 第一位获取总是会有 '{' 所以进行替换，随后的获取也总是会保留有一个空格，如 ' b'
            // 所以进行替换，去除
            return line[0].replace("{", "").replace("}", "").replace(" ", "");
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static Double censusGetIntegral(int census) {
        census = census - 1;

        String[] map = playerFishMatchIntegral.toString().split(",");
        String[] line;

        try {
            line = map[census].split("=");
            // 替换 防止其积分带有非法字符 即 '}'
            // java.lang.NumberFormatException: For input string: "12.0}"
            return Double.parseDouble(line[1].replace("{", "").replace("}", "").replace(" ", ""));
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    // 排序
    public static Map<String, Double> sortFishMatchIntegralHashMap() {
        Map<String, Double> oldMap = playerFishMatchIntegral;

        Set<Map.Entry<String, Double>> set = oldMap.entrySet();

        ArrayList<Map.Entry<String, Double>> arrayList = new ArrayList<>(set);

        arrayList.sort((arg1, arg0) -> Double.compare(arg0.getValue(), arg1.getValue()));

        LinkedHashMap<String, Double> linkedHashMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : arrayList) linkedHashMap.put(entry.getKey(), entry.getValue());

        return linkedHashMap;
    }
}
