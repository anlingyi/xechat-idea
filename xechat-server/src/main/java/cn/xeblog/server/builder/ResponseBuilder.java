package cn.xeblog.server.builder;

import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.react.result.ReactResult;
import cn.xeblog.commons.enums.MessageType;

/**
 * @author anlingyi
 * @date 2020/8/17
 */
public class ResponseBuilder {

    public static Response react(ReactResult result) {
        return build(null, result, MessageType.REACT);
    }

    public static Response system(String msg) {
        return build(null, msg, MessageType.SYSTEM);
    }

    public static Response build(User user, Object msg, MessageType messageType) {
        return new Response(user, msg, messageType);
    }

}
