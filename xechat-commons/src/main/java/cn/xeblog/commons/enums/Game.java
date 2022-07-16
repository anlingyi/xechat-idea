package cn.xeblog.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 游戏信息
 *
 * @author anlingyi
 * @date 2020/8/17
 */
@Getter
@AllArgsConstructor
public enum Game {
    GOBANG("五子棋", false),
    LANDLORDS("斗地主", false),
    NOVEL("本地阅读", false),
    LEGADO("Legado阅读", false);

    /**
     * 游戏名称
     */
    private String name;

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
