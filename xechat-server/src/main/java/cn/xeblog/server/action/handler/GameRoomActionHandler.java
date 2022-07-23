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
                roomClose(user, gameRoom);
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

    private void roomClose(User user, GameRoom gameRoom) {
        GameRoomCache.removeRoom(gameRoom.getId());

        GameRoomMsgDTO msg = new GameRoomMsgDTO();
        msg.setRoomId(gameRoom.getId());
        msg.setMsgType(GameRoomMsgDTO.MsgType.ROOM_CLOSE);

        Response resp = ResponseBuilder.build(user, msg, MessageType.GAME_ROOM);
        // 通知已收到游戏邀请但还未进入游戏房间的用户
        gameRoom.getInviteUsers().forEach(player -> player.send(resp));
        // 通知房间内的用户
        sendMsg(gameRoom, resp);
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
        Response response = ResponseBuilder.build(player, body, MessageType.GAME_ROOM);
        if (dto.getStatus() == InviteStatus.ACCEPT) {
            if (GameRoomCache.joinRoom(gameRoom.getId(), player)) {
                dto.setGameRoom(gameRoom);
                sendMsg(gameRoom, response);
            } else {
                player.setStatus(UserStatus.FISHING);
                ChannelAction.updateUserStatus(player);
                player.send(ResponseBuilder.build(null, new GameRoomMsgDTO(GameRoomMsgDTO.MsgType.GAME_ERROR, "加入游戏失败，游戏房间已满员！"), MessageType.GAME_ROOM));
            }
        } else {
            if (player.getStatus() == UserStatus.PLAYING) {
                player.setStatus(UserStatus.FISHING);
                ChannelAction.updateUserStatus(player);
            }
            // 通知房主
            gameRoom.getHomeowner().send(response);
            if (dto.getStatus() == InviteStatus.TIMEOUT) {
                // 通知玩家游戏邀请超时
                player.send(response);
            }
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
            if (gameRoom.isHomeowner(user.getUsername())) {
                roomClose(user, gameRoom);
            }
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
