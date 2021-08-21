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
    GOBANG("五子棋");

    private String name;

    public static Game getGame(int index) {
        Game[] games = values();
        if (index < 0 || index > games.length - 1) {
            return null;
        }

        return games[index];
    }

}
