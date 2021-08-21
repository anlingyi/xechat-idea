package cn.xeblog.server.handler;

import cn.xeblog.commons.entity.Request;
import cn.xeblog.server.factory.ActionHandlerFactory;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
public class RequestHandler {

    private final ChannelHandlerContext ctx;

    private final Request request;

    public RequestHandler(final ChannelHandlerContext ctx, final Request request) {
        this.ctx = ctx;
        this.request = request;
    }

    public void exec() {
        ActionHandlerFactory.INSTANCE.produce(request.getAction()).handle(ctx, request.getBody());
    }

}
