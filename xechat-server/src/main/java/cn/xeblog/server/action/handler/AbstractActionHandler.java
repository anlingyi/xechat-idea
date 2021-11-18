package cn.xeblog.server.action.handler;

import cn.xeblog.commons.entity.User;
import cn.xeblog.server.action.ChannelAction;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
public abstract class AbstractActionHandler<T> implements ActionHandler<T> {

    @Override
    public final void handle(ChannelHandlerContext ctx, T body) {
        process(ChannelAction.getUser(ctx), body);
    }

    protected abstract void process(User user, T body);

}
