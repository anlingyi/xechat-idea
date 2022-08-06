package cn.xeblog.plugin.factory;

import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.game.AbstractGame;

/**
 * @author anlingyi
 * @date 2021/8/21 9:33 下午
 */
public class GameFactory {

    private static ObjectFactory objectFactory = new ObjectFactory(DoGame.class);

    public static AbstractGame produce(Game game) {
        Object object = objectFactory.produce(game);
        if (object == null) {
            return null;
        }

        return (AbstractGame) object;
    }

}
