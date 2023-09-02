package cn.xeblog.server;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.server.config.IpRegionProperties;
import cn.xeblog.server.config.ServerConfig;
import cn.xeblog.server.handler.DefaultChannelInitializer;
import cn.xeblog.server.handler.HttpAndWebSocketChannelInitializer;
import cn.xeblog.server.service.IpRegionService;
import cn.xeblog.server.service.impl.HeFengWeatherConfigServiceImpl;
import cn.xeblog.server.service.impl.Ip2RegionServiceImpl;
import cn.xeblog.server.util.BaiDuFyUtil;
import cn.xeblog.server.util.ConfigUtil;
import cn.xeblog.server.util.IpUtil;
import cn.xeblog.server.util.SensitiveWordUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
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

/**
 * @author anlingyi
 * @date 2020/5/28
 */
@Slf4j
public class XEChatServer {

    private int port;

    /**
     * 是否开启WS协议
     */
    private boolean enableWS = true;

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
        int threads = 1;
        if (enableWS) {
            threads++;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(threads);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new DefaultChannelInitializer(sslContext))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ServerBootstrap httpAndWebSocketServer = null;
        if (enableWS) {
            httpAndWebSocketServer = new ServerBootstrap();
            httpAndWebSocketServer.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpAndWebSocketChannelInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
        }

        try {
            if (httpAndWebSocketServer != null) {
                httpAndWebSocketServer.bind(port + 1)
                        .addListener((ChannelFutureListener) future -> {
                            if (future.channel().isActive()) {
                                log.info("XEChatHTTPAndWebSocketServer Started Successfully!");
                            }
                        })
                        .sync().channel().closeFuture();
            }

            serverBootstrap.bind(port)
                    .addListener((ChannelFutureListener) future -> {
                        if (future.channel().isActive()) {
                            log.info("XEChatServer Started Successfully!");
                        }
                    })
                    .sync().channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("ERROR:", e);
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        ServerConfig serverConfig = ConfigUtil.readConfig(args);
        ServerConfig.setServerConfig(serverConfig);

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

        XEChatServer server = new XEChatServer(serverConfig.getPort());
        server.enableWS = serverConfig.getEnableWS();
        server.run();
    }
}
