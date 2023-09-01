package cn.xeblog.server.action.handler;

import cn.xeblog.commons.entity.User;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.builder.ResponseBuilder;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
public abstract class AbstractActionHandler<T> implements ActionHandler<T> {

    @Override
    public final void handle(ChannelHandlerContext ctx, T body) {
        User user = ChannelAction.getUser(ctx);
        if (user == null) {
            ctx.writeAndFlush(ResponseBuilder.system("未获取到用户信息，请先登录！"));
            ctx.close();
            return;
        }

        process(user, body);
    }

    protected abstract void process(User user, T body);

}
