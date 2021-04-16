package cn.xeblog.client;

import cn.xeblog.action.ConsoleAction;
import cn.xeblog.handler.DefaultChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author anlingyi
 * @date 2020/5/29
 */
public class XEChatClient {

    private static final String HOST = "localhost";

    public static void run() {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new DefaultChannelInitializer());
            ChannelFuture channelFuture = bootstrap.connect(HOST, 1024).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            ConsoleAction.showSimpleMsg("连接服务器失败！");
        } finally {
            group.shutdownGracefully();
        }
    }

}
