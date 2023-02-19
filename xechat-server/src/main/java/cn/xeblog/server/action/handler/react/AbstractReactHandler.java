package cn.xeblog.server.action.handler.react;

import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.react.result.ReactResult;

/**
 * @author anlingyi
 * @date 2022/9/19 8:42 AM
 */
public abstract class AbstractReactHandler<T, R> implements ReactHandler<T, R> {

    @Override
    public final void handle(User user, T body, ReactResult<R> result) {
        process(user, body, result);
    }

    protected abstract void process(User user, T body, ReactResult<R> result);

}
