package cn.xeblog.server.action.handler.react;

import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.react.result.ReactResult;

/**
 * @author anlingyi
 * @date 2022/9/19 8:43 AM
 */
public interface ReactHandler<T, R> {

    default void handle(final User user, final T body, final ReactResult<R> result) {
        // ignore
    }

}
