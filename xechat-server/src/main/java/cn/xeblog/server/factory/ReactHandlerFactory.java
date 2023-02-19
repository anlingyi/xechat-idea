package cn.xeblog.server.factory;

import cn.xeblog.commons.entity.react.React;
import cn.xeblog.commons.factory.AbstractSingletonFactory;
import cn.xeblog.server.action.handler.react.ReactHandler;
import cn.xeblog.server.annotation.DoReact;

/**
 * @author anlingyi
 * @date 2022/9/19 8:47 AM
 */
public class ReactHandlerFactory extends AbstractSingletonFactory<React, ReactHandler> {

    public static final ReactHandlerFactory INSTANCE = new ReactHandlerFactory();

    private ReactHandlerFactory() {
    }

    @Override
    protected void registration(Registry<React, ReactHandler> registry) {
        registry.addByAnnotation(DoReact.class);
    }

}
