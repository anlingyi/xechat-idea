package cn.xeblog.action.handler;

import cn.xeblog.action.AbstractAction;
import cn.xeblog.builder.ResponseBuilder;
import cn.xeblog.cache.UserCache;
import cn.xeblog.entity.User;
import cn.xeblog.enums.UserStatus;
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
