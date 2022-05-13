package cn.xeblog.plugin.cache;

import cn.xeblog.commons.enums.UserStatus;
import cn.xeblog.plugin.action.ConnectionAction;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anlingyi
 * @date 2020/6/1
 */
public class DataCache {

    public static String username;

    public static boolean isOnline;

    public static ChannelHandlerContext ctx;

    public static Map<String, String> userMap = new ConcurrentHashMap<>();

    public static ConnectionAction connectionAction;

    public static boolean reconnected;

    public static UserStatus userStatus;

}
