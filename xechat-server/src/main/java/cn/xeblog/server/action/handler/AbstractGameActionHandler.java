package cn.xeblog.server.action.handler;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.*;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.enums.UserStatus;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.cache.GameRoomCache;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
public abstract class AbstractGameActionHandler<T extends GameDTO> extends AbstractActionHandler<T> {

    @Override
    protected void process(User user, GameDTO body) {
        GameRoom gameRoom = null;
        if (StrUtil.isNotBlank(body.getRoomId())) {
            gameRoom = GameRoomCache.getGameRoom(body.getRoomId());
            if (gameRoom == null) {
                user.setStatus(UserStatus.FISHING);
                user.send(ResponseBuilder.build(null, new GameRoomMsgDTO(GameRoomMsgDTO.MsgType.GAME_ERROR, "游戏房间不存在！"), MessageType.GAME_ROOM));
                ChannelAction.updateUserStatus(user);
                return;
            }

            Game game = gameRoom.getGame();
            gameRoom.setGame(game);
            body.setGame(game);
        }

        process(user, gameRoom, (T) body);
    }

    protected abstract void process(User user, GameRoom gameRoom, T body);

}
