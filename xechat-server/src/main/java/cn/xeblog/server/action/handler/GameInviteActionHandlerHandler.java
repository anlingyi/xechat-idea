package cn.xeblog.server.action.handler;

import cn.xeblog.commons.entity.GameDTO;
import cn.xeblog.commons.entity.GameInviteResultDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.InviteStatus;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.enums.UserStatus;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.GAME_INVITE)
public class GameInviteActionHandlerHandler extends AbstractGameActionHandlerHandler<GameDTO> {

    @Override
    protected void process(User user, User opponent, GameDTO body) {
        if (opponent == null) {
            user.setStatus(UserStatus.PLAYING);
            user.send(ResponseBuilder.system("《" + body.getGame().getName() + "》游戏开始！"));
            return;
        }

        if (opponent.getStatus() != UserStatus.FISHING) {
            user.send(ResponseBuilder.build(opponent, new GameInviteResultDTO(body, InviteStatus.REJECT), MessageType.GAME_INVITE_RESULT));
            user.send(ResponseBuilder.system("人家正在" + opponent.getStatus().alias() + "呢！就你天天摸鱼？"));
            return;
        }

        user.setStatus(UserStatus.PLAYING);
        opponent.setStatus(UserStatus.PLAYING);
        opponent.send(ResponseBuilder.build(user, body, MessageType.GAME_INVITE));
        user.send(ResponseBuilder.system("已向" + opponent.getUsername() + "发送《" + body.getGame().getName() + "》游戏邀请！"));
    }

}
