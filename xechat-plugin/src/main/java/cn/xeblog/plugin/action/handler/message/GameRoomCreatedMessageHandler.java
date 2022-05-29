package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.annotation.DoMessage;

/**
 * @author anlingyi
 * @date 2022/5/25 11:22 上午
 */
@DoMessage(MessageType.GAME_ROOM_CREATED)
public class GameRoomCreatedMessageHandler extends AbstractMessageHandler<GameRoom> {

    @Override
    protected void process(Response<GameRoom> response) {
        GameRoom gameRoom = response.getBody();
        if (gameRoom == null) {
            ConsoleAction.showSystemMsg(response.getTime(), "游戏房间创建失败！");
            return;
        }

        if (GameAction.playing()) {
            GameAction.setRoomId(gameRoom.getId());
            GameAction.getAction().roomCreated(gameRoom);
        }
    }

}
