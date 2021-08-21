package cn.xeblog.server.action.handler;

import cn.xeblog.server.action.AbstractAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.cache.UserCache;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.UserStatus;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
public class LoginActionHandler extends AbstractAction<String> {

    @Override
    public void handle(ChannelHandlerContext ctx, String body) {
        String username = body;
        String id = getId(ctx);
        if (UserCache.existUsername(body)) {
            username += id;
        }

        User user = new User(username, UserStatus.FISHING, ctx.channel());
        UserCache.add(id, user);
        sendOnlineUsers();
        writeAndFlush(ResponseBuilder.system(user.getUsername() + "进入了鱼塘！"));
    }

}
