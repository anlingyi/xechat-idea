package cn.xeblog.server;

import cn.hutool.core.lang.Singleton;
import cn.nn200433.weather.service.WeatherConfigService;
import cn.nn200433.weather.service.impl.HeFengWeatherConfigServiceImpl;
import cn.xeblog.commons.util.ParamsUtils;
import cn.xeblog.server.handler.DefaultChannelInitializer;
import cn.xeblog.server.util.SensitiveWordUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.InetSocketAddress;

/**
 * @author anlingyi
 * @date 2020/5/28
 */
@Slf4j
public class XEChatServer {

    private int port;

    public XEChatServer(int port) {
        this.port = port;
    }

    private static SslContext sslContext;

    static {
        try (
                InputStream certIn = XEChatServer.class.getResourceAsStream("/ssl/server.crt");
                InputStream keyIn = XEChatServer.class.getResourceAsStream("/ssl/pkcs8_server.key");
                InputStream caIn = XEChatServer.class.getResourceAsStream("/ssl/ca.crt")
        ) {
            sslContext = SslContextBuilder.forServer(certIn, keyIn)
                    .trustManager(caIn)
                    .clientAuth(ClientAuth.REQUIRE)
                    .sslProvider(SslProvider.JDK)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new DefaultChannelInitializer(sslContext))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("error:", e);
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 1024;

        String configPort = ParamsUtils.getValue(args, "-p");
        if (configPort != null) {
            port = Integer.valueOf(args[1]);
        }
        String sensitiveWordFilePath = ParamsUtils.getValue(args, "-swfile");
        if (sensitiveWordFilePath != null) {
            SensitiveWordUtils.setSensitiveWordFilePath(sensitiveWordFilePath);
        }
        String weatherKey = ParamsUtils.getValue(args, "-weather");
        if (weatherKey != null) {
            // 实例化并单例存储
            WeatherConfigService weatherConfigService = new HeFengWeatherConfigServiceImpl(weatherKey);
            Singleton.put(weatherConfigService);
        }

        new XEChatServer(port).run();
    }
}
