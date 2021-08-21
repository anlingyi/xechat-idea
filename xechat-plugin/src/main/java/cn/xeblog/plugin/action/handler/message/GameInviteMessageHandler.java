package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.commons.entity.GameInviteDTO;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.plugin.enums.Style;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public class GameInviteMessageHandler extends AbstractGameMessageHandler<GameInviteDTO> {

    @Override
    public void handle(Response<GameInviteDTO> response) {
        User user = response.getUser();
        GameAction.setOpponent(user.getUsername());
        GameAction.setProactive(false);
        GameAction.setGame(response.getBody().getGame());

        ConsoleAction.renderText(String.format("[%s] 系统消息：%s\n", response.getTime(),
                user.getUsername() + "邀请你加入游戏-《" + GameAction.getName() +"》！"), Style.SYSTEM_MSG);
    }
}
