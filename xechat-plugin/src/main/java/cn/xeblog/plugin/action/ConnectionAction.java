package cn.xeblog.plugin.action;

import cn.hutool.core.thread.GlobalThreadPool;
import cn.xeblog.plugin.client.ClientConnectConsumer;
import cn.xeblog.plugin.client.XEChatClient;
import cn.xeblog.plugin.handler.AbstractChannelInitializer;
import cn.xeblog.plugin.handler.DefaultChannelInitializer;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author anlingyi
 * @date 2021/8/22 9:02 下午
 */
@Data
@NoArgsConstructor
public class ConnectionAction {

    private String host;

    private int port;

    private AbstractChannelInitializer channelInitializer;

    public ConnectionAction(String host, int port, AbstractChannelInitializer channelInitializer) {
        this.host = host;
        this.port = port;
        this.channelInitializer = channelInitializer;
    }

    public void exec(ClientConnectConsumer consumer) {
        if (channelInitializer == null) {
            channelInitializer = new DefaultChannelInitializer();
        }
        GlobalThreadPool.execute(() -> XEChatClient.run(this, consumer));
    }

}
