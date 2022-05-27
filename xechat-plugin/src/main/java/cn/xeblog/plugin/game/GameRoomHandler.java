package cn.xeblog.plugin.game;

import cn.xeblog.commons.entity.*;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.commons.enums.InviteStatus;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.cache.DataCache;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author anlingyi
 * @date 2022/5/25 10:18 上午
 */
public abstract class GameRoomHandler implements GameRoomEventHandler {

    private Map<String, Timer> timeoutTask = new HashMap<>();

    protected GameRoom gameRoom;

    protected boolean isHomeowner;

    /**
     * 创建游戏房间
     *
     * @param game 游戏
     * @param nums 房间人数
     */
    public void createRoom(Game game, int nums) {
        MessageAction.send(new CreateGameRoomDTO(game, nums), Action.CREATE_GAME_ROOM);
    }

    /**
     * 邀请玩家
     *
     * @param username 玩家昵称
     */
    public void invitePlayer(String username) {
        if (gameRoom == null) {
            ConsoleAction.showSimpleMsg("请先创建游戏房间！");
            return;
        }

        User player = DataCache.getUser(username);
        if (player == null) {
            ConsoleAction.showSimpleMsg("该用户不存在！");
            return;
        }

        GameRoomMsgDTO msg = new GameRoomMsgDTO();
        msg.setRoomId(gameRoom.getId());
        msg.setMsgType(GameRoomMsgDTO.MsgType.PLAYER_INVITE);
        msg.setContent(new GameInviteDTO(player.getId()));
        MessageAction.send(msg, Action.GAME_ROOM);

        Timer timer = new Timer();
        timeoutTask.put(player.getId(), timer);
        timer.schedule(new TimerTask() {
            int time = 30;

            @Override
            public void run() {
                boolean timeout = --time < 0;
                if (GameAction.isOver() || timeout) {
                    timer.cancel();
                }

                if (timeout) {
                    GameRoomMsgDTO msg = new GameRoomMsgDTO();
                    msg.setRoomId(gameRoom.getId());
                    msg.setMsgType(GameRoomMsgDTO.MsgType.PLAYER_INVITE_RESULT);
                    msg.setContent(new GameInviteResultDTO(InviteStatus.TIMEOUT, null, player.getId()));
                    MessageAction.send(msg, Action.GAME_ROOM);
                }
            }
        }, 0, 1000);
    }

    public void gameStart() {
        GameRoomMsgDTO msg = new GameRoomMsgDTO();
        msg.setRoomId(gameRoom.getId());
        msg.setMsgType(GameRoomMsgDTO.MsgType.GAME_START);
        MessageAction.send(msg, Action.GAME_ROOM);
    }

    public void gameOver() {
        GameRoomMsgDTO msg = new GameRoomMsgDTO();
        msg.setRoomId(gameRoom.getId());
        msg.setMsgType(GameRoomMsgDTO.MsgType.GAME_OVER);
        MessageAction.send(msg, Action.GAME_ROOM);
    }

    public void closeRoom() {
        GameRoomMsgDTO msg = new GameRoomMsgDTO();
        msg.setRoomId(gameRoom.getId());
        msg.setMsgType(GameRoomMsgDTO.MsgType.ROOM_CLOSE);
        MessageAction.send(msg, Action.GAME_ROOM);
    }

    public void playerReady() {
        GameRoomMsgDTO msg = new GameRoomMsgDTO();
        msg.setRoomId(gameRoom.getId());
        msg.setMsgType(GameRoomMsgDTO.MsgType.PLAYER_READY);
        MessageAction.send(msg, Action.GAME_ROOM);
    }

    @Override
    public void roomCreated(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
        this.isHomeowner = true;
        System.out.println("房间已创建：" + gameRoom);
    }

    @Override
    public void playerJoined(User player) {
        System.out.println("玩家已加入：" + player);
        gameRoom.addUser(player);
        cleanTask(player);
    }

    @Override
    public void playerInviteFailed(User player) {
        System.out.println("玩家邀请失败：" + player);
        cleanTask(player);
    }

    @Override
    public void playerLeft(User player) {
        System.out.println("玩家已离开房间：" + player);
        gameRoom.removeUser(player);
    }

    @Override
    public void playerReadied(User player) {
        System.out.println("玩家已准备：" + player);
        gameRoom.readied(player);
    }

    @Override
    public void roomOpened(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }

    @Override
    public void roomClosed() {
        System.out.println("房间已关闭：" + gameRoom);
        gameRoom = null;
    }

    @Override
    public void gameStarted(GameRoom gameRoom) {
        System.out.println("游戏已开始：" + gameRoom);
        this.gameRoom = gameRoom;
    }

    @Override
    public void gameEnded() {
        System.out.println("游戏已结束：" + gameRoom);
    }

    private void cleanTask(User player) {
        Timer timer = timeoutTask.get(player.getId());
        if (timer != null) {
            timer.cancel();
        }
    }

}
