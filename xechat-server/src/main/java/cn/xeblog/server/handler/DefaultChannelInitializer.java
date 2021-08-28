package cn.xeblog.server.handler;

import cn.xeblog.commons.codec.ProtostuffDecoder;
import cn.xeblog.commons.codec.ProtostuffEncoder;
import cn.xeblog.commons.entity.Request;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

/**
 * @author anlingyi
 * @date 2020/5/29
 */
public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslContext;

    public DefaultChannelInitializer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(sslContext.newHandler(ch.alloc()))
                .addLast(new ProtostuffDecoder(Request.class))
                .addLast(new ProtostuffEncoder())
                .addLast(new XEChatServerHandler());
    }

}
