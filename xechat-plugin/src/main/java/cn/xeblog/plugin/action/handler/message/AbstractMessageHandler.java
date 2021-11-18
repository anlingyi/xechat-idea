package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.entity.Response;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public abstract class AbstractMessageHandler<T> implements MessageHandler<T> {

    @Override
    public final void handle(Response<T> response) {
        process(response);
    }

    protected abstract void process(Response<T> response);

}
