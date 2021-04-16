package cn.xeblog.enums;

import cn.xeblog.game.AbstractGame;
import cn.xeblog.game.Gobang;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author anlingyi
 * @date 2020/8/17
 */
@Getter
@AllArgsConstructor
public enum Game {
    GOBANG("五子棋") {
        @Override
        public AbstractGame create() {
            return new Gobang();
        }
    }
    ;

    private String name;

    public abstract AbstractGame create();

    public static Game getGame(int index) {
        Game[] games = values();
        if (index < 0 || index > games.length - 1) {
            return null;
        }

        return games[index];
    }
}
