package cn.xeblog.server.action.handler;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.commons.entity.game.GameInviteResultDTO;
import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.entity.game.GameRoomMsgDTO;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.commons.enums.InviteStatus;
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
        String msg = "游戏房间不存在！";
        GameRoom gameRoom = null;
        if (StrUtil.isNotBlank(body.getRoomId())) {
            msg = "游戏房间已关闭！";
            gameRoom = GameRoomCache.getGameRoom(body.getRoomId());
        }
        if (gameRoom == null) {
            if (body instanceof GameRoomMsgDTO) {
                GameRoomMsgDTO gameRoomMsgDTO = (GameRoomMsgDTO) body;
                if (gameRoomMsgDTO.getMsgType() == GameRoomMsgDTO.MsgType.PLAYER_INVITE_RESULT) {
                    GameInviteResultDTO gameInviteResultDTO = (GameInviteResultDTO) gameRoomMsgDTO.getContent();
                    if (gameInviteResultDTO.getStatus() == InviteStatus.TIMEOUT) {
                        // 玩家邀请超时，原房间已经关闭，放弃处理
                        return;
                    }
                }
            }

            user.setStatus(UserStatus.FISHING);
            user.send(ResponseBuilder.build(null, new GameRoomMsgDTO(GameRoomMsgDTO.MsgType.GAME_ERROR, msg), MessageType.GAME_ROOM));
            ChannelAction.updateUserStatus(user);
            return;
        }

        Game game = gameRoom.getGame();
        gameRoom.setGame(game);
        body.setGame(game);

        process(user, gameRoom, (T) body);
    }

    protected abstract void process(User user, GameRoom gameRoom, T body);

}
