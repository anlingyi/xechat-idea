package cn.xeblog.server;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.server.config.IpRegionProperties;
import cn.xeblog.server.config.ServerConfig;
import cn.xeblog.server.handler.DefaultChannelInitializer;
import cn.xeblog.server.service.IpRegionService;
import cn.xeblog.server.service.impl.HeFengWeatherConfigServiceImpl;
import cn.xeblog.server.service.impl.Ip2RegionServiceImpl;
import cn.xeblog.server.util.BaiDuFyUtil;
import cn.xeblog.server.util.ConfigUtil;
import cn.xeblog.server.util.IpUtil;
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
        ServerConfig serverConfig = ConfigUtil.readConfig(args);

        final String sensitiveWordFilePath = serverConfig.getSensitiveWordPath();
        if (StrUtil.isNotBlank(sensitiveWordFilePath)) {
            SensitiveWordUtils.setSensitiveWordFilePath(sensitiveWordFilePath);
        }
        final String weatherKey = serverConfig.getWeatherApiKey();
        if (StrUtil.isNotBlank(weatherKey)) {
            // 实例化并单例存储
            Singleton.put(new HeFengWeatherConfigServiceImpl(weatherKey));
        }

        final String translationAppId = serverConfig.getTranslationAppId();
        final String translationAppKey = serverConfig.getTranslationAppKey();
        if (StrUtil.isAllNotBlank(translationAppId, translationAppKey)) {
            // 实例化并单例存储
            Singleton.put(new BaiDuFyUtil(translationAppId, translationAppKey));
        }

        final String ip2regionPath = serverConfig.getIp2RegionPath();
        if (StrUtil.isNotBlank(ip2regionPath)) {
            // 后期可根据实例化实例不同的实现
            final IpRegionService ip2RegionService = new Ip2RegionServiceImpl(IpRegionProperties.builder().ip2regionDbPath(ip2regionPath).build());

            final IpUtil ipUtil = new IpUtil(ip2RegionService);
        }

        new XEChatServer(serverConfig.getPort()).run();
    }
}
