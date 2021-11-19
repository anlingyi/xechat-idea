package cn.xeblog.server.action.handler;

import cn.xeblog.commons.enums.Action;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.commons.entity.GameInviteResultDTO;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.InviteStatus;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.enums.UserStatus;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.GAME_INVITE_RESULT)
public class GameInviteResultHandler extends AbstractGameActionHandlerHandler<GameInviteResultDTO> {

    @Override
    protected void process(User user, User opponent, GameInviteResultDTO body) {
        if (body.getStatus() != InviteStatus.ACCEPT) {
            user.setStatus(UserStatus.FISHING);
            opponent.setStatus(UserStatus.FISHING);
        }

        Response response = ResponseBuilder.build(user, body, MessageType.GAME_INVITE_RESULT);
        if (body.getStatus() != InviteStatus.REJECT) {
            user.send(response);
        }

        opponent.send(response);
    }

}
