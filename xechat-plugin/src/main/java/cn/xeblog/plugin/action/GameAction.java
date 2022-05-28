package cn.xeblog.plugin.action;

import cn.xeblog.commons.entity.game.GameDTO;
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
     * 我的昵称
     */
    private static String nickname;

    /**
     * 邀请人昵称
     */
    private static String inviter;

    /**
     * 当前游戏房间号
     */
    private static String roomId;

    public static void setRoomId(String roomId) {
        GameAction.roomId = roomId;
    }

    public static String getRoomId() {
        return roomId;
    }

    public static boolean isOfflineGame() {
        return roomId == null;
    }

    public static String getNickname() {
        return nickname;
    }

    public static void setNickname(String nickname) {
        GameAction.nickname = nickname;
    }

    public static void setGame(Game game) {
        GameAction.game = game;
    }

    public static void setInviter(String inviter) {
        GameAction.inviter = inviter;
    }

    public static String getInviter() {
        return inviter;
    }

    public static boolean isProactive() {
        return inviter == null;
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

    public static void handle(Response<GameDTO> response) {
        if (playing()) {
            action.handle(response.getBody());
        }
    }

    public static void over() {
        if (playing()) {
            action.over();
        }

        clean();
    }

    public static AbstractGame create() {
        if (game == null) {
            return null;
        }

        GameAction.action = GameFactory.produce(game);
        return action;
    }

    public static void clean() {
        game = null;
        action = null;
        inviter = null;
        nickname = null;
        roomId = null;
    }

    public static boolean playing() {
        return action != null;
    }

    public static boolean isOver() {
        return game == null;
    }

    public static AbstractGame getAction() {
        return action;
    }
}
