package cn.xeblog.server;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import cn.xeblog.commons.util.ParamsUtils;
import cn.xeblog.server.config.IpRegionProperties;
import cn.xeblog.server.constant.ConfigConstants;
import cn.xeblog.server.handler.DefaultChannelInitializer;
import cn.xeblog.server.service.impl.HeFengWeatherConfigServiceImpl;
import cn.xeblog.server.service.impl.Ip2RegionServiceImpl;
import cn.xeblog.server.util.BaiDuFy;
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
import java.nio.charset.StandardCharsets;

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
        final Setting setting = new Setting(StrUtil.blankToDefault(ParamsUtils.getValue(args, "-path"),
                "config.setting"), StandardCharsets.UTF_8, Boolean.TRUE);
        final Setting serverConfig = setting.getSetting(ConfigConstants.SERVER);
        final Setting sensitiveWordConfig = setting.getSetting(ConfigConstants.SENSITIVE_WORD);
        final Setting weatherConfig = setting.getSetting(ConfigConstants.WEATHER);
        final Setting translationConfig = setting.getSetting(ConfigConstants.TRANSLATION);
        final Setting ipConfig = setting.getSetting(ConfigConstants.IP);

        String sensitiveWordFilePath = sensitiveWordConfig.get(ConfigConstants.SENSITIVE_WORD_FILE);
        if (StrUtil.isNotBlank(sensitiveWordFilePath) && !StrUtil.equals("${SW_FILE}", sensitiveWordFilePath)) {
            SensitiveWordUtils.setSensitiveWordFilePath(sensitiveWordFilePath);
        }

        String weatherKey = weatherConfig.get(ConfigConstants.WEATHER_KEY);
        if (StrUtil.isNotBlank(weatherKey) && !StrUtil.equals("${WEATHER_KEY}", weatherKey)) {
            // 实例化并单例存储
            Singleton.put(new HeFengWeatherConfigServiceImpl(weatherKey));
        }

        String translationAppId = translationConfig.get(ConfigConstants.TRANSLATION_APP_ID);
        String translationAppKey = translationConfig.get(ConfigConstants.TRANSLATION_APP_KEY);
        if (StrUtil.isAllNotBlank(translationAppId, translationAppKey)
                && !StrUtil.equals("${BD_APP_ID}", translationAppId)
                && !StrUtil.equals("${BD_APP_KEY}", translationAppKey)) {
            // 实例化并单例存储
            Singleton.put(new BaiDuFy(translationAppId, translationAppKey));
        }

        String ip2regionPath = ipConfig.get(ConfigConstants.IP2REGION_PATH);
        if (StrUtil.isNotBlank(ip2regionPath) && !StrUtil.equals("${IP2REGION_PATH}", ip2regionPath)) {
            final Ip2RegionServiceImpl ip2RegionService = new Ip2RegionServiceImpl(IpRegionProperties.builder().ip2regionDbPath(ip2regionPath).build());
            final IpUtil ipUtil = new IpUtil(ip2RegionService);
        }

        new XEChatServer(serverConfig.getInt(ConfigConstants.SERVER_PORT)).run();
    }
}
