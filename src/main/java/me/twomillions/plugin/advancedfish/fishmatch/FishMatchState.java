package me.twomillions.plugin.advancedfish.fishmatch;

/**
 * author:     2000000
 * project:    AdvancedFish
 * package:        me.twomillions.plugin.advancedfish.fishmatch
 * enumName:      FishMatchState
 * date:    2022/11/9 4:42
 */
public enum FishMatchState {
    // 需要使用到的真实状态
    NONE, HOLD, START, ENDED, START_COUNTDOWN,

    // NONE - 没有比赛正在进行
    // HOLD - 比赛正在等待
    // START - 比赛开始
    // ENDED - 比赛结束
    // START_COUNTDOWN - 比赛冷却

    // 需要用到的给予效果的状态，而不用实际设置的
    COUNTDOWN, COUNTDOWN_END, PLAYER_JOIN, PLAYER_REJOIN, PLAYER_LEAVE, INSUFFICIENT

    // COUNTDOWN - 比赛开始倒计时
    // COUNTDOWN_END - 比赛结束倒计时
    // PLAYER_JOIN - 玩家进入
    // PLAYER_REJOIN - 玩家断线重连
    // PLAYER_LEAVE - 玩家退出
    // INSUFFICIENT - 人数不足
}
