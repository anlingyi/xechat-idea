package cn.xeblog.server.action.handler;

import cn.xeblog.commons.entity.GameDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.enums.UserStatus;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.GAME_OVER)
public class GameOverActionHandlerHandler extends AbstractGameActionHandlerHandler<GameDTO> {

    @Override
    protected void process(User user, User opponent, GameDTO body) {
        user.setStatus(UserStatus.FISHING);
        Response resp = ResponseBuilder.build(user, body, MessageType.GAME_OVER);
        user.send(resp);

        if (opponent != null) {
            opponent.setStatus(UserStatus.FISHING);
            opponent.send(resp);
        }
    }

}
