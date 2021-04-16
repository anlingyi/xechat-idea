package cn.xeblog.enums;

import cn.xeblog.action.handler.message.*;
import cn.xeblog.factory.MessageHandlerFactory;

/**
 * @author anlingyi
 * @date 2020/6/1
 */
public enum MessageType implements MessageHandlerFactory {
    USER {
        @Override
        public MessageHandler produce() {
            return new UserMessageHandler();
        }
    },
    SYSTEM {
        @Override
        public MessageHandler produce() {
            return new SystemMessageHandler();
        }
    },
    ONLINE_USERS {
        @Override
        public MessageHandler produce() {
            return new OnlineUsersMessageHandler();
        }
    },
    GAME {
        @Override
        public MessageHandler produce() {
            return new GameMessageHandler();
        }
    },
    GAME_INVITE {
        @Override
        public MessageHandler produce() {
            return new GameInviteMessageHandler();
        }
    },
    GAME_INVITE_RESULT {
        @Override
        public MessageHandler produce() {
            return new GameInviteResultMessageHandler();
        }
    },
    GAME_OVER {
        @Override
        public MessageHandler produce() {
            return new GameOverMessageHandler();
        }
    }
    ;
}
