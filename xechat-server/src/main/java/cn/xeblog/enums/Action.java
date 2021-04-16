package cn.xeblog.enums;

import cn.xeblog.action.handler.*;
import cn.xeblog.factory.ActionHandlerFactory;

/**
 * @author anlingyi
 * @date 2020/6/1
 */
public enum Action implements ActionHandlerFactory {
    LOGIN {
        @Override
        public ActionHandler produce() {
            return new LoginActionHandler();
        }
    },
    CHAT {
        @Override
        public ActionHandler produce() {
            return new ChatActionHandler();
        }
    },
    GAME {
        @Override
        public ActionHandler produce() {
            return new GameActionHandler();
        }
    },
    SET_STATUS {
        @Override
        public ActionHandler produce() {
            return new SetStatusActionHandler();
        }
    },
    GAME_INVITE {
        @Override
        public ActionHandler produce() {
            return new GameInviteActionHandler();
        }
    },
    GAME_INVITE_RESULT {
        @Override
        public ActionHandler produce() {
            return new GameInviteResultHandler();
        }
    },
    GAME_OVER {
        @Override
        public ActionHandler produce() {
            return new GameOverActionHandler();
        }
    }
    ;
}
