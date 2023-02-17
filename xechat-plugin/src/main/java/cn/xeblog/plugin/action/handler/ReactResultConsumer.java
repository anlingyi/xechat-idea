package cn.xeblog.plugin.action.handler;

/**
 * @author anlingyi
 * @date 2023/2/17 10:39 PM
 */
public interface ReactResultConsumer<T> {

    void succeed(T body);

    void failed(String msg);

}
