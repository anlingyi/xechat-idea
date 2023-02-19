package cn.xeblog.plugin.handler;

import cn.xeblog.commons.codec.ProtostuffDecoder;
import cn.xeblog.commons.codec.ProtostuffEncoder;
import cn.xeblog.commons.entity.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import java.io.InputStream;

/**
 * @author anlingyi
 * @date 2023/2/17 9:52 PM
 */
public abstract class AbstractChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static SslContext sslContext;

    static {
        try (
                InputStream certIn = AbstractChannelInitializer.class.getResourceAsStream("/ssl/client.crt");
                InputStream keyIn = AbstractChannelInitializer.class.getResourceAsStream("/ssl/pkcs8_client.key");
                InputStream caIn = AbstractChannelInitializer.class.getResourceAsStream("/ssl/ca.crt")
        ) {
            sslContext = SslContextBuilder.forClient()
                    .keyManager(certIn, keyIn)
                    .trustManager(caIn)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        ChannelHandler idleStateHandler = addIdleStateHandler();
        if (idleStateHandler != null) {
            pipeline.addLast(idleStateHandler);
        }
        pipeline.addLast(sslContext.newHandler(ch.alloc()));
        pipeline.addLast(new ProtostuffDecoder(Response.class));
        pipeline.addLast(new ProtostuffEncoder());
        pipeline.addLast(addClientHandler());
    };

    public abstract ChannelHandler addClientHandler();

    public ChannelHandler addIdleStateHandler() {
        return null;
    }

}
