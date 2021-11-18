package cn.xeblog.server.action.handler;

import cn.xeblog.commons.enums.Action;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.commons.entity.GameDTO;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.MessageType;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.GAME)
public class GameActionHandlerHandler extends AbstractGameActionHandlerHandler<GameDTO> {

    @Override
    protected void process(User user, User opponent, GameDTO body) {
        opponent.send(ResponseBuilder.build(user, body, MessageType.GAME));
    }

}
