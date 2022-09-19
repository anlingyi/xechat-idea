package cn.xeblog.server.action.handler.react;

import cn.xeblog.commons.entity.User;

/**
 * @author anlingyi
 * @date 2022/9/19 8:42 AM
 */
public abstract class AbstractReactHandler<T> implements ReactHandler<T> {

    @Override
    public final void handle(User user, T request) {
        process(user, request);
    }

    protected abstract void process(User user, T request);

}
