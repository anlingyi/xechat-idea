package cn.xeblog.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author anlingyi
 * @date 2020/5/29
 */
public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final int OBJECT_MAX_SIZE = 1024 * 1024;

    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new ObjectDecoder(OBJECT_MAX_SIZE, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())))
                .addLast(new ObjectEncoder())
                .addLast(new XEChatServerHandler());
    }

}
