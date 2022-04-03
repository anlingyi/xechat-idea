package cn.xeblog.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author anlingyi
 * @date 2020/8/17
 */
@Getter
@AllArgsConstructor
public enum Game {
    GOBANG("五子棋", 0, 1, false);

    /**
     * 游戏名称
     */
    private String name;

    /**
     * 最小邀请玩家数
     */
    private int minPlayers;

    /**
     * 最大邀请玩家数
     */
    private int maxPlayers;

    /**
     * 是否必须要登录
     */
    private boolean requiredLogin;

    public static Game getGame(int index) {
        Game[] games = values();
        if (index < 0 || index >= games.length) {
            return null;
        }

        return games[index];
    }

}
