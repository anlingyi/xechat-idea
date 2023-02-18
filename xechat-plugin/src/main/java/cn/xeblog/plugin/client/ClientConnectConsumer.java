package cn.xeblog.plugin.client;

import cn.hutool.core.thread.GlobalThreadPool;
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
    default void succeed(Channel channel) {
        GlobalThreadPool.execute(() -> doSucceed(channel));
    }

    /**
     * 客户端连接失败
     */
    default void failed() {
        GlobalThreadPool.execute(this::doFailed);
    }

    void doSucceed(Channel channel);

    void doFailed();

}
