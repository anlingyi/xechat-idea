package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.commons.entity.game.GameRoomMsgDTO;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.commons.enums.UserStatus;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.enums.Command;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoCommand(Command.GAME_OVER)
public class GameOverCommandHandler extends AbstractCommandHandler {

    @Override
    public void process(String[] args) {
        Game game = GameAction.getGame();
        if (game == null) {
            ConsoleAction.showSimpleMsg("结束个寂寞？");
            return;
        }

        String roomId = GameAction.getRoomId();
        if (roomId != null) {
            if (!GameAction.playing()) {
                Command.JOIN.exec("1");
                return;
            }

            GameRoomMsgDTO msg = new GameRoomMsgDTO();
            msg.setRoomId(roomId);
            msg.setMsgType(GameRoomMsgDTO.MsgType.PLAYER_LEFT);
            MessageAction.send(msg, Action.GAME_ROOM);
        } else if (DataCache.isOnline) {
            UserStatus lastStatus = DataCache.userStatus;
            MessageAction.send(lastStatus == null ? UserStatus.FISHING : lastStatus, Action.SET_STATUS);
        }

        ConsoleAction.showSimpleMsg(game.getName() + "游戏结束！");
        GameAction.over();
    }

    @Override
    protected boolean check(String[] args) {
        return true;
    }

}
