package me.suxuan.advancedfish.runnable;

import de.leonhard.storage.Yaml;
import me.suxuan.advancedfish.fishmatch.FishMatchManager;
import me.suxuan.advancedfish.fishmatch.FishMatchState;
import me.suxuan.advancedfish.main;
import me.suxuan.advancedfish.manager.ConfigManager;
import me.suxuan.advancedfish.manager.EffectSendManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.runnable
 * className:      FishMatchStartRunnable
 * date:    2022/11/9 12:09
 */
public class FishMatchRunnable {
    private static final Plugin plugin = main.getInstance();
    
    public static void startRunnable(Player player, FishMatchState state, Integer countDown) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // 如果比赛并没有开始则不再执行
                if (state == FishMatchState.NONE) {
                    cancel();
                    return;
                }

                String stateString = state.toString();

                switch (state) {
                    // 根据不同的状态发送不同的效果给玩家
                    case HOLD:
                        if (FishMatchManager.getFishMatchState() != state) FishMatchManager.setFishMatchState(state);
                        // 当举办的时候清空上一次的 Map 记录
                        FishMatchManager.playerFishMatchIntegral.clear();
                        FishMatchManager.deleteFishMatchPlayerDataDirectory();

                        if (!FishMatchManager.isPlayerInTheFishMatch(player)) FishMatchManager.addPlayerToFishMatch(player);
                        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> EffectSendManager.sendEffect("fishMatch", onlinePlayer, null, "HOLD", player, null));

                        // 消息重复部分
                        if (ConfigManager.getFishMatchYaml().getBoolean("LOOP.ENABLE")) {
                            int time = (ConfigManager.getFishMatchYaml().getInt("LOOP.SECONDS") + 2) * 20;
                            startMessageLoopRunnable(ConfigManager.getFishMatchYaml().getBoolean("JOINED-PLAYER-SHOW-HOLD"), time, time);
                        }

                        break;
                    case COUNTDOWN_END:
                    case COUNTDOWN:
                        // 倒计时
                        if (FishMatchManager.getFishMatchState() != state) FishMatchManager.setFishMatchState(state);
                        FishMatchManager.getFishMatchPlayers().forEach(fishMatchPlayers -> Bukkit.getOnlinePlayers().forEach(onlinePlayer -> EffectSendManager.sendEffect("fishMatch", fishMatchPlayers, null, stateString, null, countDown)));
                       
                        break;
                    case START:
                        // 开启部分
                        if (FishMatchManager.getFishMatchState() != state) FishMatchManager.setFishMatchState(state);
                        FishMatchManager.getFishMatchPlayers().forEach(fishMatchPlayers -> Bukkit.getOnlinePlayers().forEach(onlinePlayer -> EffectSendManager.sendEffect("fishMatch", fishMatchPlayers, null, stateString, null, null)));
                        
                        break;
                    case ENDED:
                        // 结束部分
                        if (FishMatchManager.getFishMatchState() != state) FishMatchManager.setFishMatchState(state);
                        FishMatchManager.getFishMatchPlayers().forEach(fishMatchPlayers -> Bukkit.getOnlinePlayers().forEach(onlinePlayer -> EffectSendManager.sendEffect("fishMatch", fishMatchPlayers, null, stateString, null, null)));

                        // 如果是结束则将记录全部添加进 Map 内
                        // 并清空已记录的玩家列表与 Json 临时文件
                        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                            // 逻辑问题修复
                            if (!FishMatchManager.isFishMatchPlayerDataExists(onlinePlayer)) return;
                            FishMatchManager.getPlayerFishMatchIntegral().put(onlinePlayer.getName(), FishMatchManager.getJsonFishMatchPlayerIntegral(onlinePlayer));
                        });

                        // 排序
                        FishMatchManager.playerFishMatchIntegral = FishMatchManager.sortFishMatchIntegralHashMap();
                        
                        // 给予奖励
                        startRewardGive();
                        
                        // 清除 文件在开始后再删除
                        FishMatchManager.getFishMatchPlayers().clear();

                        // 设置为冷却，在 FishMatchManager 中会在冷却时间过后设置为 NONE
                        FishMatchManager.setFishMatchState(FishMatchState.START_COUNTDOWN);

                        break;
                    case PLAYER_JOIN:
                        // 玩家进入
                        FishMatchManager.addPlayerToFishMatch(player);
                        FishMatchManager.getFishMatchPlayers().forEach(fishMatchPlayers -> {
                            Player showPlayer = ConfigManager.getFishMatchYaml().getBoolean("PLAYER-JOIN-SHOW-ALL-PLAYER") ? fishMatchPlayers : player;
                            EffectSendManager.sendEffect("fishMatch", showPlayer, null, stateString, player, null);
                        });

                        break;
                    case PLAYER_REJOIN:
                        EffectSendManager.sendEffect("fishMatch", player, null, stateString, player, null);

                        break;
                    case PLAYER_LEAVE:
                        // 玩家离开
                        FishMatchManager.removePlayerAtFishMatch(player);
                        EffectSendManager.sendEffect("fishMatch", player, null, stateString, player, null);

                        break;
                    case INSUFFICIENT:
                        // 当人数不够后，发送对应的效果，并清空已记录状态的玩家
                        FishMatchManager.setFishMatchState(FishMatchState.NONE);
                        FishMatchManager.getFishMatchPlayers().forEach(fishMatchPlayers -> EffectSendManager.sendEffect("fishMatch", fishMatchPlayers, null, stateString, null, null));
                        FishMatchManager.getFishMatchPlayers().clear();
                        
                        break;
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    // 消息循环
    private static void startMessageLoopRunnable(boolean sendOnlinePlayer, long delay, long period) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // 如果不再是等待状态，则不再重复发送消息
                if (FishMatchManager.getFishMatchState() != FishMatchState.HOLD) {
                    cancel();
                    return;
                }

                Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                    if (!sendOnlinePlayer) if (FishMatchManager.isPlayerInTheFishMatch(onlinePlayer)) return;
                    EffectSendManager.sendEffect("fishMatch", onlinePlayer, null, "HOLD", onlinePlayer, null);
                });
            }
        }.runTaskTimerAsynchronously(plugin, delay, period);
    }

    // 物品给予
    private static void startRewardGive() {
        Yaml yaml = ConfigManager.getFishMatchYaml();
        int later = yaml.getInt("LATER");

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            yaml.setPathPrefix("REWARDGIVE");
            
            yaml.getStringList("PLAYER").forEach(configString -> {
                String[] configStringSplit = configString.split(";");
                String number = configStringSplit[1];
                int ranking = Integer.parseInt(configStringSplit[0]);

                // 如果不是参与奖
                if (!number.toLowerCase(Locale.ROOT).equals("default")) {
                    String playerName = FishMatchManager.censusGetPlayer(ranking);
                    Player player = playerName == null ? null : Bukkit.getPlayerExact(playerName);

                    if (player == null) return;
                    
                    EffectSendManager.sendEffect("fishMatch", player, null, "REWARDGIVE." + number, null, null);

                    return;
                }

                // 发送参与奖
                // 这里使用的是所有玩家进行遍历，是为了防止玩家有积分，且积分达到奖励标准但是却不小心退出的玩家
                Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                    Integer onlinePlayerRanking = FishMatchManager.playerNameGetCensus(onlinePlayer.getName());
                    Double onlinePlayerIntegral = onlinePlayerRanking == null ? null : FishMatchManager.censusGetIntegral(onlinePlayerRanking);

                    // 筛选排除
                    if (onlinePlayerRanking == null || onlinePlayerIntegral == null || onlinePlayerRanking < ranking) return;

                    // 为不够积分的玩家显示
                    if (onlinePlayerIntegral < yaml.getDouble("DEFAULT-MIN")) {
                        EffectSendManager.sendEffect("fishMatch", onlinePlayer, null, "REWARDGIVE.default-min-effect", null, null);
                        return;
                    }
                    
                    EffectSendManager.sendEffect("fishMatch", onlinePlayer, null, "REWARDGIVE." + number, null, null);
                });
            });
        }, later);
    }
    
    // 开始倒计时
    public static void startCountDownRunnable(Player player) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> FishMatchRunnable.startRunnable(player, FishMatchState.COUNTDOWN, 10), 50 * 20);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> FishMatchRunnable.startRunnable(player, FishMatchState.COUNTDOWN, 5), 55 * 20);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> FishMatchRunnable.startRunnable(player, FishMatchState.COUNTDOWN, 4), 56 * 20);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> FishMatchRunnable.startRunnable(player, FishMatchState.COUNTDOWN, 3), 57 * 20);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> FishMatchRunnable.startRunnable(player, FishMatchState.COUNTDOWN, 2), 58 * 20);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> FishMatchRunnable.startRunnable(player, FishMatchState.COUNTDOWN, 1), 59 * 20);
    }
    
    // 结束倒计时
    public static void endedCountDownRunnable(Player player, int time) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> FishMatchRunnable.startRunnable(player, FishMatchState.COUNTDOWN_END, 10), ((long) time * 60 * 20) - (10 * 20) + (60 * 20));
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> FishMatchRunnable.startRunnable(player, FishMatchState.COUNTDOWN_END, 5), ((long) time * 60 * 20) - (5 * 20) + (60 * 20));
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> FishMatchRunnable.startRunnable(player, FishMatchState.COUNTDOWN_END, 4), ((long) time * 60 * 20) - (4 * 20) + (60 * 20));
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> FishMatchRunnable.startRunnable(player, FishMatchState.COUNTDOWN_END, 3), ((long) time * 60 * 20) - (3 * 20) + (60 * 20));
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> FishMatchRunnable.startRunnable(player, FishMatchState.COUNTDOWN_END, 2), ((long) time * 60 * 20) - (2 * 20) + (60 * 20));
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> FishMatchRunnable.startRunnable(player, FishMatchState.COUNTDOWN_END, 1), ((long) time * 60 * 20) - (20) + (60 * 20));
    }
}
