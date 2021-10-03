package cn.xeblog.server.action.handler;

import cn.xeblog.commons.enums.Action;
import cn.xeblog.server.action.AbstractAction;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.commons.enums.MessageType;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.CHAT)
public class ChatActionHandler extends AbstractAction<String> {

    @Override
    public void handle(ChannelHandlerContext ctx, String body) {
        writeAndFlush(ResponseBuilder.build(getUser(ctx), body, MessageType.USER));
    }
}
