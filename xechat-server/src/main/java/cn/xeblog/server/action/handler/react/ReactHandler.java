package cn.xeblog.server.action.handler.react;

import cn.xeblog.commons.entity.User;

/**
 * @author anlingyi
 * @date 2022/9/19 8:43 AM
 */
public interface ReactHandler<T> {

    default void handle(final User user, final T request) {
        // ignore
    }

}
