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
        if (channel.isActive()) {
            channel.writeAndFlush(request);
        }
    }

    public static void send(Object body, Action action) {
        send(RequestBuilder.build(body, action));
    }

}
