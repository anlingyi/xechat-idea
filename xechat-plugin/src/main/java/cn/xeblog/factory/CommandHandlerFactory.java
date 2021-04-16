package cn.xeblog.factory;

import cn.xeblog.action.handler.command.CommandHandler;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public interface CommandHandlerFactory {

    CommandHandler produce();

}
