package cn.xeblog.server.handler;

import cn.xeblog.commons.entity.Request;
import cn.xeblog.commons.enums.Protocol;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author anlingyi
 * @date 2020/5/29
 */
@Slf4j
public class XEChatServerHandler extends AbstractDefaultChannelHandler<Request> {

    protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
        msg.setProtocol(Protocol.DEFAULT);
        new RequestHandler(ctx, msg).exec();
    }

}