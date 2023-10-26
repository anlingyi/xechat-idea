package cn.xeblog.plugin.tools.encourage.cache;

import cn.xeblog.commons.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存区
 *
 * @author 鼓励师
 * @date 2023/10/20 16:21
 */
public class EncourageCache {

    /**
     * 艾特用户缓存
     */
    public static final List<User> atUsers = new ArrayList<>();

    /**
     * 屏蔽用户缓存 里面存了userName和uuid
     */
    public static final List<String> BLOCK_USER_CACHE = new ArrayList<>(32);

    /**
     * 显示提示消息
     */
    public static boolean showTips = false;
    /**
     * 私聊用户
     */
    public static User privateChatUser = null;

    /**
     * 服务端是否支持在线统计
     */
    public static boolean supportStatistics = false;
    /**
     * 服务端是否支持私聊
     */
    public static boolean supportPrivateChat = false;

    public static boolean checkBlock(User user) {
        if (user == null) {
            return false;
        }

        String username = user.getUsername();
        String uuid = user.getUuid();
        return EncourageCache.BLOCK_USER_CACHE.contains(username) || EncourageCache.BLOCK_USER_CACHE.contains(uuid);
    }

}
