package cn.xeblog.builder;

import cn.xeblog.entity.Response;
import cn.xeblog.entity.User;
import cn.xeblog.enums.MessageType;

/**
 * @author anlingyi
 * @date 2020/8/17
 */
public class ResponseBuilder {

    public static Response system(String msg) {
        return new Response(null, msg, MessageType.SYSTEM);
    }

    public static Response build(User user, Object msg, MessageType messageType) {
        return new Response(user, msg, messageType);
    }

}
