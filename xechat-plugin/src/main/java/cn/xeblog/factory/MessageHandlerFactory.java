package cn.xeblog.factory;

import cn.xeblog.action.handler.message.MessageHandler;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public interface MessageHandlerFactory {

    MessageHandler produce();

}
