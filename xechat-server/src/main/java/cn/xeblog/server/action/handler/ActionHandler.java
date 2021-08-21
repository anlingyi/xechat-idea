package cn.xeblog.server.action.handler;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
public interface ActionHandler<T> {

    default void handle(final ChannelHandlerContext ctx, final T body) {
        // ignore
    }

}
