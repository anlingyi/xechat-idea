package cn.xeblog.server.cache;

import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.UserStatus;
import cn.xeblog.server.action.ChannelAction;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anlingyi
 * @date 2022/5/25 10:44 上午
 */
@Slf4j
public class GameRoomCache {

    /**
     * 游戏房间缓存，key -> roomId
     */
    private static final Map<String, GameRoom> GAME_ROOM_MAP = new ConcurrentHashMap<>(32);

    /**
     * 玩家当前所在游戏房间缓存，key -> username
     */
    private static final Map<String, GameRoom> USER_ROOM_MAP = new ConcurrentHashMap<>(32);

    /**
     * 抢占房间
     *
     * @param roomId 房间ID
     * @return GameRoom 为null表示房间抢占失败
     */
    public static GameRoom seize(String roomId) {
        if (existRoom(roomId)) {
            return null;
        }

        GameRoom gameRoom = new GameRoom();
        gameRoom.setId(roomId);
        if (GAME_ROOM_MAP.put(roomId, gameRoom) == null) {
            return gameRoom;
        }

        return null;
    }

    /**
     * 移除房间
     *
     * @param roomId 房间ID
     */
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
            userSet.forEach(player -> {
                if (gameRoom.isHomeowner(player.getUsername())) {
                    return;
                }

                player.setStatus(UserStatus.FISHING);
                ChannelAction.updateUserStatus(player);
            });
        }
    }

    /**
     * 判断房间是否存在
     *
     * @param roomId 房间ID
     * @return
     */
    public static boolean existRoom(String roomId) {
        return GAME_ROOM_MAP.containsKey(roomId);
    }

    /**
     * 玩家加入房间
     *
     * @param roomId 房间ID
     * @param user   玩家
     * @return
     */
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

    /**
     * 玩家离开房间
     *
     * @param roomId 房间ID
     * @param user   玩家
     * @return
     */
    public static boolean leftRoom(String roomId, User user) {
        GameRoom gameRoom = GAME_ROOM_MAP.get(roomId);
        if (gameRoom == null) {
            return false;
        }

        if (gameRoom.removeUser(user)) {
            USER_ROOM_MAP.remove(user.getId());
            if (gameRoom.getCurrentNums() == 0 || gameRoom.isHomeowner(user.getUsername())) {
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
