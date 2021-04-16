package cn.xeblog.action;

import cn.xeblog.cache.DataCache;
import cn.xeblog.entity.Request;
import io.netty.channel.Channel;

/**
 * @author anlingyi
 * @date 2020/6/1
 */
public class MessageAction {

    public static void send(Request request) {
        Channel channel = DataCache.ctx.channel();
        channel.writeAndFlush(request);
    }

}
