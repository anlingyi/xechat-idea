package cn.xeblog.commons.enums;

/**
 * 客户端动作
 *
 * @author anlingyi
 * @date 2020/6/1
 */
public enum Action {
    /**
     * 登录
     */
    LOGIN,
    /**
     * 聊天
     */
    CHAT,
    /**
     * 游戏
     */
    GAME,
    /**
     * 设置状态
     */
    SET_STATUS,
    /**
     * 游戏结束
     */
    GAME_OVER,
    /**
     * 游戏房间
     */
    GAME_ROOM,
    /**
     * 创建游戏房间
     */
    CREATE_GAME_ROOM,
    /**
     * 在线用户列表
     */
    LIST_USERS,
    /**
     * 心跳
     */
    HEARTBEAT;
}
