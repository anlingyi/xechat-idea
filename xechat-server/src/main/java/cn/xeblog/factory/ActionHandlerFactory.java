package cn.xeblog.factory;

import cn.xeblog.action.handler.ActionHandler;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
public interface ActionHandlerFactory {

    ActionHandler produce();

}
