package cn.xeblog.plugin.handler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author anlingyi
 * @date 2020/5/29
 */
public class DefaultChannelInitializer extends AbstractChannelInitializer {

    @Override
    public ChannelHandler addClientHandler() {
        return new XEChatClientHandler();
    }

    @Override
    public ChannelHandler addIdleStateHandler() {
        return new IdleStateHandler(0, 5, 23);
    }

}
