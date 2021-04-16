package cn.xeblog.action.handler.message;

import cn.xeblog.action.ConsoleAction;
import cn.xeblog.action.GameAction;
import cn.xeblog.entity.GameInviteResultDTO;
import cn.xeblog.entity.Response;
import cn.xeblog.enums.Style;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
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
