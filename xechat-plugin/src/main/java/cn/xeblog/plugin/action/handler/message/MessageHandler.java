package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.entity.Response;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public interface MessageHandler<T> {

    default void handle(final Response<T> response) {
        // ignore
    }

}
