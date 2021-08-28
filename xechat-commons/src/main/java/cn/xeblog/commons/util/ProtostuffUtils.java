package cn.xeblog.commons.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * 基于Protostuff的序列化工具
 *
 * @author anlingyi
 * @date 2021/8/28 5:17 下午
 */
public class ProtostuffUtils {

    public static <T> byte[] serialize(T obj) {
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate();
        try {
            return ProtobufIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T msg = schema.newMessage();
        ProtobufIOUtil.mergeFrom(bytes, msg, schema);
        return msg;
    }

}
