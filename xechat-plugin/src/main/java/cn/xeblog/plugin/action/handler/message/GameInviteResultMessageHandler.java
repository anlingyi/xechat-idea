package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.commons.entity.GameInviteResultDTO;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.plugin.annotation.DoMessage;
import cn.xeblog.plugin.enums.Style;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoMessage(MessageType.GAME_INVITE_RESULT)
public class GameInviteResultMessageHandler extends AbstractGameMessageHandler<GameInviteResultDTO> {

    @Override
    public void handle(Response<GameInviteResultDTO> response) {
        GameInviteResultDTO result = response.getBody();
        String opponent = GameAction.getOpponent();
        switch (result.getStatus()) {
            case ACCEPT:
                ConsoleAction.renderText(String.format("[%s] 系统消息：%s\n", response.getTime(),
                        opponent + "已加入游戏！"), Style.SYSTEM_MSG);
                GameAction.create();
                break;
            case REJECT:
                ConsoleAction.renderText(String.format("[%s] 系统消息：%s\n", response.getTime(),
                        opponent + "拒绝和你一起游戏！"), Style.SYSTEM_MSG);
                GameAction.clean();
                break;
            case TIMEOUT:
                ConsoleAction.renderText(String.format("[%s] 系统消息：%s\n", response.getTime(), "邀请超时！"),
                        Style.SYSTEM_MSG);
                GameAction.clean();
                break;
        }
    }
}
