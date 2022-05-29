package cn.xeblog.commons.entity.game;

import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;

/**
 * @author anlingyi
 * @date 2022/5/25 10:30 上午
 */
@Data
public class GameRoom implements Serializable {

    /**
     * 房间号
     */
    private String id;

    /**
     * 游戏
     */
    private Game game;

    /**
     * 几人房
     */
    private int nums;

    /**
     * 房主
     */
    private User homeowner;

    /**
     * 房间内玩家
     */
    private Map<String, Player> users = new LinkedHashMap<>();

    /**
     * 邀请用户列表
     */
    private transient Set<User> inviteUsers = new HashSet<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Player {
        /**
         * 玩家ID
         */
        private String id;
        /**
         * 玩家昵称
         */
        private String username;
        /**
         * 是否已准备
         */
        private boolean readied;

        public Player(String id, String username) {
            this.id = id;
            this.username = username;
        }

    }

    public boolean addUser(User user) {
        synchronized (users) {
            if (getCurrentNums() > nums - 1) {
                return false;
            }

            if (existUser(user)) {
                return false;
            }

            users.put(user.getUsername(), new Player(user.getId(), user.getUsername()));
            return true;
        }
    }

    private boolean existUser(User user) {
        return users.get(user.getUsername()) != null;
    }

    public boolean removeUser(User user) {
        synchronized (users) {
            return users.remove(user.getUsername()) != null;
        }
    }

    public int getCurrentNums() {
        return users.size();
    }

    public void addInviteUser(User user) {
        synchronized (inviteUsers) {
            inviteUsers.add(user);
        }
    }

    public void removeInviteUser(User user) {
        synchronized (inviteUsers) {
            inviteUsers.remove(user);
        }
    }

    public boolean readied(User user) {
        Player player = users.get(user.getUsername());
        if (player == null) {
            return false;
        }

        player.setReadied(true);
        return true;
    }

    public boolean readyCancelled(User user) {
        Player player = users.get(user.getUsername());
        if (player == null) {
            return false;
        }

        player.setReadied(false);
        return true;
    }

    public boolean isHomeowner(String username) {
        return homeowner.getUsername().equals(username);
    }

    public boolean isOvered() {
        return nums <= getCurrentNums();
    }

}
