package cn.xeblog.plugin.client;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.handler.DefaultChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import java.io.InputStream;
import java.util.function.Consumer;

/**
 * @author anlingyi
 * @date 2020/5/29
 */
public class XEChatClient {

    private static final String HOST = "localhost";
    private static final int PORT = 1024;

    private static SslContext sslContext;

    static {
        try (
                InputStream certIn = XEChatClient.class.getResourceAsStream("/ssl/client.crt");
                InputStream keyIn = XEChatClient.class.getResourceAsStream("/ssl/pkcs8_client.key");
                InputStream caIn = XEChatClient.class.getResourceAsStream("/ssl/ca.crt")
        ) {
            sslContext = SslContextBuilder.forClient()
                    .keyManager(certIn, keyIn)
                    .trustManager(caIn)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void run() {
        run(HOST, PORT, null);
    }

    public static void run(String host, int port, Consumer<Boolean> consumer) {
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
                    .handler(new DefaultChannelInitializer(sslContext));
            ChannelFuture channelFuture = bootstrap.connect(host, port).addListener(l -> {
                if (consumer != null) {
                    consumer.accept(l.isSuccess());
                }
            }).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            ConsoleAction.showSimpleMsg("连接服务器失败！");
        } finally {
            group.shutdownGracefully();
        }
    }

}
