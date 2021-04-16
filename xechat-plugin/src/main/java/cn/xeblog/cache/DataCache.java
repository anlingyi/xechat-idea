package cn.xeblog.cache;

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
    
}
