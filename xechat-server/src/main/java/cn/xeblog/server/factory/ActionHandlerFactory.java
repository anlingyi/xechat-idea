package cn.xeblog.server.factory;

import cn.xeblog.commons.factory.AbstractSingletonFactory;
import cn.xeblog.server.action.handler.*;
import cn.xeblog.commons.enums.Action;

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
        registry.add(Action.LOGIN, LoginActionHandler.class);
        registry.add(Action.CHAT, ChatActionHandler.class);
        registry.add(Action.GAME, GameActionHandler.class);
        registry.add(Action.SET_STATUS, SetStatusActionHandler.class);
        registry.add(Action.GAME_INVITE, GameInviteActionHandler.class);
        registry.add(Action.GAME_INVITE_RESULT, GameInviteResultHandler.class);
        registry.add(Action.GAME_OVER, GameOverActionHandler.class);
    }

}
