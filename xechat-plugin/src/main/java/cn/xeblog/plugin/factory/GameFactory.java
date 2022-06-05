package cn.xeblog.plugin.factory;

import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.gobang.Gobang;
import cn.xeblog.plugin.game.landlords.LandlordsGame;

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
            case LANDLORDS:
                return new LandlordsGame();
        }

        return null;
    }

}
