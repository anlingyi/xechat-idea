package cn.xeblog.plugin.client;

import cn.xeblog.plugin.handler.AbstractChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author anlingyi
 * @date 2020/5/29
 */
public class XEChatClient {

    private static final String HOST = "localhost";
    private static final int PORT = 1024;

    public static void run(String host, int port, AbstractChannelInitializer channelInitializer, ClientConnectConsumer consumer) {
        if (host == null) {
            host = HOST;
        }
        if (port == 0) {
            port = PORT;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .handler(channelInitializer);
            ChannelFuture channelFuture = bootstrap.connect(host, port)
                    .addListener((ChannelFutureListener) future -> {
                        Channel channel = future.channel();
                        if (channel.isActive()) {
                            consumer.succeed(channel);
                        }
                    })
                    .sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            consumer.failed();
        } finally {
            group.shutdownGracefully();
        }
    }

}
