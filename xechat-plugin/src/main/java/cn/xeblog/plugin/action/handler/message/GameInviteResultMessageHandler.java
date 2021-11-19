package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.commons.entity.GameInviteResultDTO;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.plugin.annotation.DoMessage;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoMessage(MessageType.GAME_INVITE_RESULT)
public class GameInviteResultMessageHandler extends AbstractGameMessageHandler<GameInviteResultDTO> {

    @Override
    protected void process(Response<GameInviteResultDTO> response) {
        GameInviteResultDTO result = response.getBody();
        String opponent = GameAction.getOpponent();
        switch (result.getStatus()) {
            case ACCEPT:
                ConsoleAction.showSystemMsg(response.getTime(), opponent + "已加入游戏！");
                GameAction.create();
                return;
            case REJECT:
                ConsoleAction.showSystemMsg(response.getTime(), opponent + "拒绝和你一起游戏！");
                break;
            case TIMEOUT:
                ConsoleAction.showSystemMsg(response.getTime(), "邀请超时！");
                break;
            case OFFLINE:
                ConsoleAction.showSystemMsg(response.getTime(), "对方已经下线了！");
                break;
        }

        GameAction.clean();
    }

}
