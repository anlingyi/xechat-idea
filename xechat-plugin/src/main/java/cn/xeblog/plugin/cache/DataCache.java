package cn.xeblog.plugin.cache;

import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.UserStatus;
import cn.xeblog.plugin.action.ConnectionAction;
import com.intellij.openapi.project.Project;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anlingyi
 * @date 2020/6/1
 */
public class DataCache {

    /**
     * 当前登录的用户名
     */
    public static String username;

    /**
     * 是否在线
     */
    public static boolean isOnline;

    /**
     * 通道处理上下文
     */
    public static ChannelHandlerContext ctx;

    /**
     * 当前在线用户缓存，key -> username
     */
    public static Map<String, User> userMap = new ConcurrentHashMap<>();

    /**
     * 服务器连接数据缓存
     */
    public static ConnectionAction connectionAction;

    /**
     * 断线重连标记
     */
    public static boolean reconnected;

    /**
     * 用户状态设置缓存
     */
    public static UserStatus userStatus;

    /**
     * 当前项目
     */
    public static Project project;

    /**
     * 获取用户信息
     *
     * @param username 用户名
     * @return
     */
    public static User getUser(String username) {
        return userMap.get(username);
    }

    /**
     * 获取当前用户信息
     *
     * @return
     */
    public static User getCurrentUser() {
        return getUser(username);
    }

}
