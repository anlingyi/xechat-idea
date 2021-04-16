package cn.xeblog.action.handler;

import cn.xeblog.action.AbstractAction;
import cn.xeblog.builder.ResponseBuilder;
import cn.xeblog.enums.MessageType;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
public class ChatActionHandler extends AbstractAction<String> {

    @Override
    public void handle(ChannelHandlerContext ctx, String body) {
        writeAndFlush(ResponseBuilder.build(getUser(ctx), body, MessageType.USER));
    }
}
