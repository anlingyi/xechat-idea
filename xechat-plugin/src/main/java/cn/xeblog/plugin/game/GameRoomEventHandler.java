package cn.xeblog.plugin.game;

import cn.xeblog.commons.entity.GameRoom;
import cn.xeblog.commons.entity.User;

/**
 * @author anlingyi
 * @date 2022/5/25 10:18 上午
 */
public interface GameRoomEventHandler {

    /**
     * 房间已创建
     *
     * @param gameRoom
     */
    void roomCreated(GameRoom gameRoom);

    /**
     * 玩家已加入
     *
     * @param player
     */
    void playerJoined(User player);

    /**
     * 玩家邀请失败
     *
     * @param player
     */
    void playerInviteFailed(User player);

    /**
     * 玩家已离开
     *
     * @param player
     */
    void playerLeft(User player);

    /**
     * 玩家已准备
     *
     * @param player
     */
    void playerReadied(User player);

    /**
     * 房间已开启
     *
     * @param gameRoom
     */
    void roomOpened(GameRoom gameRoom);

    /**
     * 房间已关闭
     */
    void roomClosed();

    /**
     * 游戏已开始
     *
     * @param gameRoom
     */
    void gameStarted(GameRoom gameRoom);

    /**
     * 游戏已结束
     */
    void gameEnded();

}
