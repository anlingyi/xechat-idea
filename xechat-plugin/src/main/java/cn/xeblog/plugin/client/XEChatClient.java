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

import javax.net.ssl.SSLException;
import java.io.File;

/**
 * @author anlingyi
 * @date 2020/5/29
 */
public class XEChatClient {

    private static final String HOST = "localhost";
    private static final int PORT = 1024;

    private static final File CERT_CHAIN_FILE = new File(XEChatClient.class.getResource("/ssl/client.crt").getFile());
    private static final File KEY_FILE = new File(XEChatClient.class.getResource("/ssl/pkcs8_client.key").getFile());
    private static final File ROOT_FILE = new File(XEChatClient.class.getResource("/ssl/ca.crt").getFile());

    private static SslContext sslContext;

    static {
        try {
            sslContext = SslContextBuilder.forClient()
                    .keyManager(CERT_CHAIN_FILE, KEY_FILE)
                    .trustManager(ROOT_FILE)
                    .build();
        } catch (SSLException e) {
            e.printStackTrace();
        }
    }

    public static void run() {
        run(HOST, PORT);
    }

    public static void run(String host, int port) {
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
                    .handler(new DefaultChannelInitializer(sslContext));
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            ConsoleAction.showSimpleMsg("连接服务器失败！");
        } finally {
            group.shutdownGracefully();
        }
    }

}
