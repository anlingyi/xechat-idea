package cn.xeblog.commons.codec;

import cn.xeblog.commons.util.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 基于Protostuff的编码器
 *
 * @author anlingyi
 * @date 2021/8/28 6:11 下午
 */
public class ProtostuffEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(ProtostuffUtils.serialize(msg));
    }

}
