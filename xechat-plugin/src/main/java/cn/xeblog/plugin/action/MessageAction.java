package cn.xeblog.plugin.action;

import cn.xeblog.commons.enums.Action;
import cn.xeblog.plugin.builder.RequestBuilder;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.commons.entity.Request;
import io.netty.channel.Channel;

/**
 * @author anlingyi
 * @date 2020/6/1
 */
public class MessageAction {

    public static void send(Request request) {
        Channel channel = DataCache.ctx.channel();
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(request).addListener(l -> {
                if (!l.isSuccess() && request.getAction() == Action.CHAT) {
                    ConsoleAction.showSimpleMsg("消息发送失败啦~");
                }
            });
        } else {
            ConsoleAction.showSimpleMsg("似乎已经和服务器失联了？");
        }
    }

    public static void send(Object body, Action action) {
        send(RequestBuilder.build(body, action));
    }

}
