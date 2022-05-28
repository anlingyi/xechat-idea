package cn.xeblog.server.action.handler;

import cn.xeblog.commons.entity.*;
import cn.xeblog.commons.entity.game.GameInviteDTO;
import cn.xeblog.commons.entity.game.GameInviteResultDTO;
import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.entity.game.GameRoomMsgDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.InviteStatus;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.enums.UserStatus;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.cache.GameRoomCache;
import cn.xeblog.server.cache.UserCache;

/**
 * @author anlingyi
 * @date 2022/5/25 3:26 下午
 */
@DoAction(Action.GAME_ROOM)
public class GameRoomActionHandler extends AbstractGameActionHandler<GameRoomMsgDTO> {

    @Override
    protected void process(User user, GameRoom gameRoom, GameRoomMsgDTO body) {
        switch (body.getMsgType()) {
            case PLAYER_LEFT:
                playerLeft(user, gameRoom, body);
                break;
            case PLAYER_INVITE:
                playerInvite(user, gameRoom, body);
                break;
            case PLAYER_INVITE_RESULT:
                playerInviteResult(user, gameRoom, body);
                break;
            case ROOM_CLOSE:
                GameRoomCache.removeRoom(gameRoom.getId());
                sendMsg(gameRoom, ResponseBuilder.build(user, body, MessageType.GAME_ROOM));
                break;
            case GAME_START:
                gameRoom.getUsers().forEach((k, v) -> v.setReadied(false));
                body.setContent(gameRoom);
                sendMsg(gameRoom, ResponseBuilder.build(user, body, MessageType.GAME_ROOM));
                break;
            case PLAYER_READY:
                gameRoom.readied(user);
                sendMsg(gameRoom, ResponseBuilder.build(user, body, MessageType.GAME_ROOM));
                break;
            case PLAYER_CANCEL_READY:
                gameRoom.readyCancelled(user);
                sendMsg(gameRoom, ResponseBuilder.build(user, body, MessageType.GAME_ROOM));
                break;
            case GAME_OVER:
                user.send(ResponseBuilder.build(user, body, MessageType.GAME_ROOM));
                break;
        }
    }

    private void playerInviteResult(User user, GameRoom gameRoom, GameRoomMsgDTO body) {
        GameInviteResultDTO dto = (GameInviteResultDTO) body.getContent();
        User player = user;
        if (dto.getPlayerId() != null) {
            player = UserCache.get(dto.getPlayerId());
            if (player == null) {
                return;
            }
        }

        gameRoom.removeInviteUser(player);
        Response response = ResponseBuilder.build(user, body, MessageType.GAME_ROOM);
        if (dto.getStatus() == InviteStatus.ACCEPT) {
            if (GameRoomCache.joinRoom(gameRoom.getId(), user)) {
                dto.setGameRoom(gameRoom);
                sendMsg(gameRoom, response);
            } else {
                player.send(ResponseBuilder.build(null, new GameRoomMsgDTO(GameRoomMsgDTO.MsgType.GAME_ERROR, "加入游戏房间失败！"), MessageType.GAME_ROOM));
            }
        } else {
            player.setStatus(UserStatus.FISHING);
            ChannelAction.updateUserStatus(player);
            user.send(response);
            player.send(response);
        }
    }

    private void playerInvite(User user, GameRoom gameRoom, GameRoomMsgDTO body) {
        GameInviteDTO dto = (GameInviteDTO) body.getContent();
        User player = UserCache.get(dto.getPlayerId());
        if (player == null) {
            user.send(ResponseBuilder.system("该邀请用户不存在！"));
            return;
        }

        GameRoomMsgDTO msg = new GameRoomMsgDTO();
        msg.setGame(gameRoom.getGame());
        msg.setRoomId(gameRoom.getId());
        if (player.getStatus() != UserStatus.FISHING) {
            msg.setMsgType(GameRoomMsgDTO.MsgType.PLAYER_INVITE_RESULT);
            msg.setContent(new GameInviteResultDTO(InviteStatus.REJECT, null, null));
            user.send(ResponseBuilder.build(player, msg, MessageType.GAME_ROOM));
            user.send(ResponseBuilder.system("人家正在" + player.getStatus().alias() + "呢！就你天天摸鱼？"));
            return;
        }

        gameRoom.addInviteUser(player);
        player.setStatus(UserStatus.PLAYING);
        ChannelAction.updateUserStatus(player);

        msg.setMsgType(GameRoomMsgDTO.MsgType.PLAYER_INVITE);
        player.send(ResponseBuilder.build(user, msg, MessageType.GAME_ROOM));
        user.send(ResponseBuilder.system("已向" + player.getUsername() + "发送《" + gameRoom.getGame().getName() + "》游戏邀请！"));
    }

    private void playerLeft(User user, GameRoom gameRoom, GameRoomMsgDTO body) {
        user.setStatus(UserStatus.FISHING);
        ChannelAction.updateUserStatus(user);

        if (GameRoomCache.leftRoom(gameRoom.getId(), user)) {
            Response resp = ResponseBuilder.build(user, body, MessageType.GAME_ROOM);
            sendMsg(gameRoom, resp);
        }
    }

    private void sendMsg(GameRoom gameRoom, Response response) {
        gameRoom.getUsers().forEach((k, v) -> {
            User player = UserCache.get(v.getId());
            if (player != null) {
                player.send(response);
            }
        });
    }

}
