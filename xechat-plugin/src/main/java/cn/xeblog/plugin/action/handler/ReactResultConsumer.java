package cn.xeblog.plugin.action.handler;

/**
 * @author anlingyi
 * @date 2023/2/17 10:39 PM
 */
public interface ReactResultConsumer<T> {

    default void succeed(T body) {
        doSucceed(body);
    }

    default void failed(String msg) {
        doFailed(msg);
    }

    void doSucceed(T body);

    void doFailed(String msg);
}
