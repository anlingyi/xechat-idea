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
        // 标记当前的读索引位置
        byteBuf.markReaderIndex();

        // 可读字节数必须大于一个int类型长度
        if (byteBuf.readableBytes() > 4) {
            // 获取消息体长度
            int length = byteBuf.readInt();
            if (byteBuf.readableBytes() < length) {
                // 数据是不完整的，重置当前的读索引到标记位置
                byteBuf.resetReaderIndex();
            } else {
                // 读取完整的数据
                byte[] data = new byte[length];
                byteBuf.readRetainedSlice(length).readBytes(data);
                list.add(ProtostuffUtils.deserialize(data, clazz));
            }
        }
    }

}
