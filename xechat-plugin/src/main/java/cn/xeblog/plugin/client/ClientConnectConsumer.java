package cn.xeblog.plugin.client;

import io.netty.channel.Channel;

/**
 * @author anlingyi
 * @date 2023/2/17 10:17 PM
 */
public interface ClientConnectConsumer {

    /**
     * 客户端连接成功
     *
     * @param channel
     */
    void succeed(Channel channel);

    /**
     * 客户端连接失败
     */
    void failed();

}
