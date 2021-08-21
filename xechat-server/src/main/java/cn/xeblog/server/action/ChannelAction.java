package cn.xeblog.server.action;

import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.cache.UserCache;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@Slf4j
public class ChannelAction {

    private static final ChannelGroup GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void writeAndFlush(Object resp) {
        GROUP.writeAndFlush(resp);
    }

    public static void add(Channel channel) {
        GROUP.add(channel);
    }

    public static String getId(ChannelHandlerContext ctx) {
        return ctx.channel().id().asShortText();
    }

    public static User getUser(ChannelHandlerContext ctx) {
        return getUser(getId(ctx));
    }

    public static User getUser(String id) {
        return UserCache.get(id);
    }

    public static void sendOnlineUsers() {
        writeAndFlush(ResponseBuilder.build(null, UserCache.getUsernameMap(), MessageType.ONLINE_USERS));
    }

    public static void cleanUser(ChannelHandlerContext ctx) {
        cleanUser(getId(ctx));
    }

    public static User cleanUser(String id) {
        log.debug("清理用户, id -> {}", id);

        User user = getUser(id);
        if (user == null) {
            return null;
        }

        log.debug("清理用户, username -> {}", user.getUsername());

        UserCache.remove(id);
        sendOnlineUsers();
        writeAndFlush(ResponseBuilder.system(user.getUsername() + "离开了鱼塘！"));

        return user;
    }
}
