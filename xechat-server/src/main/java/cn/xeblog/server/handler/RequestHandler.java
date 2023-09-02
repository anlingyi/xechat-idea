package cn.xeblog.server.handler;

import cn.hutool.core.thread.GlobalThreadPool;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.xeblog.commons.entity.Request;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.Protocol;
import cn.xeblog.commons.enums.UserStatus;
import cn.xeblog.server.action.handler.ActionHandler;
import cn.xeblog.server.builder.ResponseBuilder;
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
        if (request.getAction() == null || request.getAction() == Action.HEARTBEAT) {
            return;
        }

        if (ObjectUtil.isEmpty(request.getBody())) {
            ctx.writeAndFlush(ResponseBuilder.system("Body is null!"));
            return;
        }

        GlobalThreadPool.execute(() -> {
            ActionHandler produce = ActionHandlerFactory.INSTANCE.produce(request.getAction());
            Object body = request.getBody();

            // 非默认协议需要转换body的数据类型
            if (request.getProtocol() != Protocol.DEFAULT) {
                try {
                    if (request.getAction() == Action.SET_STATUS) {
                        body = UserStatus.valueOf(body.toString());
                    } else {
                        body = JSONUtil.toBean(body.toString(), ClassUtil.getTypeArgument(produce.getClass()));
                    }
                } catch (Exception e) {
                    ctx.writeAndFlush(ResponseBuilder.system("消息内容解析异常!"));
                    return;
                }
            }

            produce.handle(ctx, body);
        });
    }

}
