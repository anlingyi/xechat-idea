package cn.xeblog.plugin.factory;

import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.Gobang;

/**
 * @author anlingyi
 * @date 2021/8/21 9:33 下午
 */
public class GameFactory {

    private GameFactory() {
    }

    public static AbstractGame produce(Game game) {
        switch (game) {
            case GOBANG:
                return new Gobang();
        }

        return null;
    }

}
