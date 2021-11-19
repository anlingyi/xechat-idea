package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.entity.GameDTO;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.plugin.annotation.DoMessage;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoMessage(MessageType.GAME_INVITE)
public class GameInviteMessageHandler extends AbstractGameMessageHandler<GameDTO> {

    @Override
    protected void process(Response<GameDTO> response) {
        User user = response.getUser();
        GameAction.setOpponent(user.getUsername());
        GameAction.setGame(response.getBody().getGame());

        ConsoleAction.showSystemMsg(response.getTime(),
                user.getUsername() + "邀请你加入游戏-《" + GameAction.getName() + "》！");
    }

}
