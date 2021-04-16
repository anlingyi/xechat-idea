package cn.xeblog.action;

import cn.xeblog.entity.Response;
import cn.xeblog.enums.Game;
import cn.xeblog.game.AbstractGame;

/**
 * @author anlingyi
 * @date 2020/8/31
 */
public class GameAction {

    private static Game game;

    private static AbstractGame action;

    private static String opponent;

    private static boolean proactive;

    public static void setGame(Game game) {
        GameAction.game = game;
    }

    public static void setAction(AbstractGame action) {
        GameAction.action = action;
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

        GameAction.action = game.create();
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
}
