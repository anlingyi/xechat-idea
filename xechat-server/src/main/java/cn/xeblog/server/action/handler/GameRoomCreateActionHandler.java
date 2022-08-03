package cn.xeblog.server.action.handler;

import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.game.CreateGameRoomDTO;
import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.cache.GameRoomCache;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author anlingyi
 * @date 2022/5/25 10:41 上午
 */
@Slf4j
@DoAction(Action.CREATE_GAME_ROOM)
public class GameRoomCreateActionHandler extends AbstractActionHandler<CreateGameRoomDTO> {

    @Override
    protected void process(User user, CreateGameRoomDTO body) {
        String roomId = generateRoomId();
        GameRoom gameRoom = GameRoomCache.seize(roomId);
        Response<GameRoom> response = ResponseBuilder.build(null, gameRoom, MessageType.GAME_ROOM_CREATED);
        if (gameRoom == null) {
            log.debug("游戏房间创建失败 -> roomId: {}", roomId);
            user.send(response);
            return;
        }

        gameRoom.setGame(body.getGame());
        gameRoom.setNums(body.getNums());
        gameRoom.setGameMode(body.getGameMode());
        gameRoom.setHomeowner(user);
        GameRoomCache.joinRoom(roomId, user);
        user.send(response);
        log.debug("游戏房间创建成功 -> {}", gameRoom);
    }

    private static String generateRoomId() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmssSSS"));
    }

}
