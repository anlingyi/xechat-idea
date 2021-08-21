package cn.xeblog.server.cache;

import cn.xeblog.commons.entity.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anlingyi
 * @date 2020/5/29
 */
public final class UserCache {

    private static final Map<String, User> USER_MAP = new ConcurrentHashMap(32);
    private static final Map<String, String> USERNAME_MAP = new ConcurrentHashMap(32);

    private UserCache() {

    }

    public static void add(String key, User user) {
        USER_MAP.put(key, user);
        USERNAME_MAP.put(user.getUsername(), key);
    }

    public static User get(String key) {
        return USER_MAP.get(key);
    }

    public static void remove(String key) {
        User user = USER_MAP.get(key);
        if (user == null) {
            return;
        }

        USER_MAP.remove(key);
        USERNAME_MAP.remove(user.getUsername());
    }

    public static void clear() {
        USER_MAP.clear();
    }

    public static int size() {
        return USER_MAP.size();
    }

    public static Map<String, String> getUsernameMap() {
        return USERNAME_MAP;
    }

    public static boolean existUsername(String username) {
        return USERNAME_MAP.get(username) != null;
    }

}
