package cn.xeblog.plugin.factory;

import cn.xeblog.commons.enums.Game;
import cn.xeblog.commons.util.ClassUtils;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.game.AbstractGame;
import com.intellij.util.PathUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author anlingyi
 * @date 2021/8/21 9:33 下午
 */
public class GameFactory {

    private static final Map<Game, Class> GAME_CLASS_MAP = new HashMap<>();

    private GameFactory() {
    }

    static {
        Set<Class<?>> clazzSet = ClassUtils.scan(PathUtil.getJarPathForClass(GameFactory.class), null, DoGame.class);
        if (!clazzSet.isEmpty()) {
            clazzSet.forEach(clazz -> {
                DoGame doGame = clazz.getAnnotation(DoGame.class);
                GAME_CLASS_MAP.put(doGame.value(), clazz);
            });
        }
    }

    public static AbstractGame produce(Game game) {
        Class clazz = GAME_CLASS_MAP.get(game);
        if (clazz != null) {
            try {
                return (AbstractGame) clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
