package cn.xeblog.action.handler.message;

import cn.xeblog.action.ConsoleAction;
import cn.xeblog.action.GameAction;
import cn.xeblog.cache.DataCache;
import cn.xeblog.entity.GameDTO;
import cn.xeblog.entity.GameInviteDTO;
import cn.xeblog.entity.Response;
import cn.xeblog.entity.User;
import cn.xeblog.enums.Style;

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
