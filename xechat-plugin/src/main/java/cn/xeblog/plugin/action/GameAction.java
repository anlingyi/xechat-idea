package cn.xeblog.plugin.action;

import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.factory.GameFactory;
import cn.xeblog.plugin.game.AbstractGame;

/**
 * @author anlingyi
 * @date 2020/8/31
 */
public class GameAction {

    /**
     * 当前游戏
     */
    private static Game game;

    /**
     * 游戏动作
     */
    private static AbstractGame action;

    /**
     * 对手昵称
     */
    private static String opponent;

    /**
     * 是否是主动邀请
     */
    private static boolean proactive;

    public static void setGame(Game game) {
        GameAction.game = game;
    }

    public static void setOpponent(String opponent) {
        GameAction.opponent = opponent;
    }

    public static void setProactive(boolean proactive) {
        GameAction.proactive = proactive;
    }

    public static String getOpponent() {
        return opponent;
    }

    public static boolean isProactive() {
        return proactive;
    }

    public static Game getGame() {
        return game;
    }

    public static String getName() {
        if (game == null) {
            return "";
        }

        return game.getName();
    }

    public static void handle(Response response) {
        if (playing()) {
            action.handle(response);
        }
    }

    public static void over() {
        if (playing()) {
            action.over();
        }

        clean();
    }

    public static void create() {
        if (game == null) {
            return;
        }

        GameAction.action = GameFactory.produce(game);
    }

    public static void clean() {
        game = null;
        action = null;
        opponent = null;
        proactive = false;
    }

    public static boolean playing() {
        return action != null;
    }

    public static boolean isOver() {
        return game == null;
    }
}
