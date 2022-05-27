package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.entity.GameRoom;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.enums.MessageType;
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
        if (GameAction.playing()) {
            GameRoom gameRoom = response.getBody();
            GameAction.setRoomId(gameRoom.getId());
            GameAction.getAction().roomCreated(gameRoom);
        }
    }

}
