package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.game.GameInviteResultDTO;
import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.entity.game.GameRoomMsgDTO;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.annotation.DoMessage;
import cn.xeblog.plugin.cache.DataCache;

/**
 * @author anlingyi
 * @date 2022/5/25 3:42 下午
 */
@DoMessage(MessageType.GAME_ROOM)
public class GameRoomMessageHandler extends AbstractGameMessageHandler<GameRoomMsgDTO> {

    @Override
    protected void process(Response<GameRoomMsgDTO> response) {
        GameRoomMsgDTO body = response.getBody();
        switch (body.getMsgType()) {
            case PLAYER_LEFT:
                playerLeft(response);
                break;
            case PLAYER_INVITE:
                playerInvite(response);
                break;
            case PLAYER_INVITE_RESULT:
                playerInviteResult(response);
                break;
            case PLAYER_READY:
                playerReady(response);
                break;
            case GAME_START:
                gameStart(response);
                break;
            case GAME_OVER:
                gameOver(response);
                break;
            case GAME_ERROR:
                gameError(response);
                break;
            case ROOM_CLOSE:
                roomClosed(response);
                break;
        }
    }

    private void playerReady(Response<GameRoomMsgDTO> response) {
        GameAction.getAction().playerReadied(response.getUser());
    }

    private void roomClosed(Response<GameRoomMsgDTO> response) {
        User player = response.getUser();
        ConsoleAction.showSystemMsg(response.getTime(), player.getUsername() + "关闭了房间！");
        GameAction.getAction().roomClosed();
    }

    private void gameStart(Response<GameRoomMsgDTO> response) {
        GameRoomMsgDTO msg = response.getBody();
        ConsoleAction.showSystemMsg(response.getTime(), msg.getGame().getName() + "游戏开始！");
        GameAction.getAction().gameStarted((GameRoom) msg.getContent());
    }

    private void playerLeft(Response<GameRoomMsgDTO> response) {
        User player = response.getUser();
        ConsoleAction.showSystemMsg(response.getTime(), player.getUsername() + "退出了游戏！");
        GameAction.getAction().playerLeft(player);
    }

    private void playerInvite(Response<GameRoomMsgDTO> response) {
        GameRoomMsgDTO msg = response.getBody();
        User user = response.getUser();
        GameAction.setRoomId(msg.getRoomId());
        GameAction.setNickname(DataCache.username);
        GameAction.setInviter(user.getUsername());
        GameAction.setGame(msg.getGame());

        ConsoleAction.showSystemMsg(response.getTime(),
                user.getUsername() + "邀请你加入游戏-《" + GameAction.getName() + "》！");
    }

    private void playerInviteResult(Response<GameRoomMsgDTO> response) {
        GameRoomMsgDTO msg = response.getBody();
        GameInviteResultDTO result = (GameInviteResultDTO) msg.getContent();
        User player = response.getUser();
        switch (result.getStatus()) {
            case ACCEPT:
                ConsoleAction.showSystemMsg(response.getTime(), player.getUsername() + "已加入游戏！");
                if (!GameAction.playing()) {
                    GameRoom gameRoom = result.getGameRoom();
                    GameAction.create().roomOpened(gameRoom);
                }
                GameAction.getAction().playerJoined(player);
                return;
            case REJECT:
                ConsoleAction.showSystemMsg(response.getTime(), player.getUsername() + "拒绝和你一起游戏！");
                break;
            case TIMEOUT:
                boolean isMe = GameAction.getNickname() == null || player.getUsername().equals(GameAction.getNickname());
                String text = isMe ? "游戏邀请已经超时！" : player.getUsername() + "游戏邀请超时！";
                ConsoleAction.showSystemMsg(response.getTime(), text);
                if (isMe) {
                    GameAction.clean();
                    return;
                }

                break;
            case OFFLINE:
                ConsoleAction.showSystemMsg(response.getTime(), "对方已经下线了！");
                return;
        }

        GameAction.getAction().playerInviteFailed(player);
    }

    private void gameOver(Response<GameRoomMsgDTO> response) {
        GameAction.getAction().gameEnded();
    }

    private void gameError(Response<GameRoomMsgDTO> response) {
        String content = (String) response.getBody().getContent();
        ConsoleAction.showSystemMsg(response.getTime(), content);
        if (GameAction.playing()) {
            GameAction.getAction().roomClosed();
        }
        GameAction.clean();
    }

}
