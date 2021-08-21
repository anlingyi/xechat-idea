package cn.xeblog.plugin.factory;

import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.factory.AbstractSingletonFactory;
import cn.xeblog.plugin.action.handler.message.*;

/**
 * @author anlingyi
 * @date 2021/8/21 8:39 下午
 */
public class MessageHandlerFactory extends AbstractSingletonFactory<MessageType, MessageHandler> {

    public static final MessageHandlerFactory INSTANCE = new MessageHandlerFactory();

    private MessageHandlerFactory() {
    }

    @Override
    protected void registration(Registry<MessageType, MessageHandler> registry) {
        registry.add(MessageType.USER, UserMessageHandler.class);
        registry.add(MessageType.SYSTEM, SystemMessageHandler.class);
        registry.add(MessageType.ONLINE_USERS, OnlineUsersMessageHandler.class);
        registry.add(MessageType.GAME, GameMessageHandler.class);
        registry.add(MessageType.GAME_INVITE, GameInviteMessageHandler.class);
        registry.add(MessageType.GAME_INVITE_RESULT, GameInviteResultMessageHandler.class);
        registry.add(MessageType.GAME_OVER, GameOverMessageHandler.class);
    }

}
