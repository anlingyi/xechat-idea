package cn.xeblog.commons.codec;

import cn.xeblog.commons.util.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 基于Protostuff的解码器
 *
 * @author anlingyi
 * @date 2021/8/28 6:11 下午
 */
public class ProtostuffDecoder extends ByteToMessageDecoder {

    private final Class clazz;

    public ProtostuffDecoder(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byteBuf.markReaderIndex();
        int preIndex = byteBuf.readerIndex();
        int length = byteBuf.readInt();

        if (preIndex == byteBuf.readerIndex()) {
            return;
        }

        if (length > 0) {
            if (byteBuf.readableBytes() < length) {
                byteBuf.resetReaderIndex();
            } else {
                byte[] data = new byte[length];
                byteBuf.readRetainedSlice(length).readBytes(data);
                list.add(ProtostuffUtils.deserialize(data, clazz));
            }
        }
    }

}
