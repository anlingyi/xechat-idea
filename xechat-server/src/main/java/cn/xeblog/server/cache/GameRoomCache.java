package cn.xeblog.server.cache;

import cn.xeblog.commons.entity.GameRoom;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.UserStatus;
import cn.xeblog.server.action.ChannelAction;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anlingyi
 * @date 2022/5/25 10:44 上午
 */
@Slf4j
public class GameRoomCache {

    private static final Map<String, GameRoom> GAME_ROOM_MAP = new ConcurrentHashMap<>(32);

    private static final Map<String, GameRoom> USER_ROOM_MAP = new ConcurrentHashMap<>(32);

    public static GameRoom seize(String rooId) {
        synchronized (GAME_ROOM_MAP) {
            if (existRoom(rooId)) {
                return null;
            }

            GameRoom gameRoom = new GameRoom();
            gameRoom.setId(rooId);
            GAME_ROOM_MAP.put(rooId, gameRoom);
            return gameRoom;
        }
    }

    public static void addRoom(GameRoom gameRoom) {
        GAME_ROOM_MAP.put(gameRoom.getId(), gameRoom);
    }

    public static void removeRoom(String roomId) {
        GameRoom gameRoom = getGameRoom(roomId);
        if (gameRoom == null) {
            return;
        }

        log.debug("游戏房间关闭 -> {}", gameRoom);

        GAME_ROOM_MAP.remove(roomId);
        if (gameRoom.getUsers().size() > 0) {
            gameRoom.getUsers().forEach((k, v) -> {
                USER_ROOM_MAP.remove(v.getId());
            });
        }

        Set<User> userSet = gameRoom.getInviteUsers();
        gameRoom.getUsers().forEach((k, v) -> {
            User user = UserCache.get(v.getId());
            if (user != null) {
                userSet.add(user);
            }
        });
        if (userSet.size() > 0) {
            gameRoom.getInviteUsers().forEach(player -> {
                if (player.getUsername().equals(gameRoom.getHomeowner())) {
                    return;
                }

                player.setStatus(UserStatus.FISHING);
                ChannelAction.updateUserStatus(player);
            });
        }
    }

    public static boolean existRoom(String roomId) {
        return GAME_ROOM_MAP.containsKey(roomId);
    }

    public static boolean joinRoom(String roomId, User user) {
        GameRoom gameRoom = GAME_ROOM_MAP.get(roomId);
        if (gameRoom == null) {
            return false;
        }

        if (USER_ROOM_MAP.containsKey(user.getId())) {
            return false;
        }

        if (gameRoom.addUser(user)) {
            USER_ROOM_MAP.put(user.getId(), gameRoom);
            return true;
        }

        return false;
    }

    public static boolean leftRoom(String roomId, User user) {
        GameRoom gameRoom = GAME_ROOM_MAP.get(roomId);
        if (gameRoom == null) {
            return false;
        }

        if (gameRoom.removeUser(user)) {
            USER_ROOM_MAP.remove(user.getId());
            if (gameRoom.getCurrentNums() == 0 || user.getUsername().equals(gameRoom.getHomeowner())) {
                removeRoom(gameRoom.getId());
            }
            return true;
        }

        return false;
    }

    public static GameRoom getGameRoom(String roomId) {
        return GAME_ROOM_MAP.get(roomId);
    }

    public static GameRoom getGameRoomByUserId(String userId) {
        return USER_ROOM_MAP.get(userId);
    }

}
