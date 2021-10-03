package cn.xeblog.plugin.factory;

import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.handler.message.*;
import cn.xeblog.plugin.annotation.DoMessage;

/**
 * @author anlingyi
 * @date 2021/8/21 8:39 下午
 */
public class MessageHandlerFactory extends AbstractIdeaSingletonFactory<MessageType, MessageHandler> {

    public static final MessageHandlerFactory INSTANCE = new MessageHandlerFactory();

    private MessageHandlerFactory() {
    }

    @Override
    protected void registration(Registry<MessageType, MessageHandler> registry) {
        registry.addByAnnotation(DoMessage.class);
    }

}
