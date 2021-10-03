package cn.xeblog.server.factory;

import cn.xeblog.commons.factory.AbstractSingletonFactory;
import cn.xeblog.server.action.handler.*;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.server.annotation.DoAction;

/**
 * @author anlingyi
 * @date 2021/8/21 7:07 下午
 */
public class ActionHandlerFactory extends AbstractSingletonFactory<Action, ActionHandler> {

    public static final ActionHandlerFactory INSTANCE = new ActionHandlerFactory();

    private ActionHandlerFactory() {
    }

    @Override
    protected void registration(Registry<Action, ActionHandler> registry) {
        registry.addByAnnotation(DoAction.class);
    }

}
