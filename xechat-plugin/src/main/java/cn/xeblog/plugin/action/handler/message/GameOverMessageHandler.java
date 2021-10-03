package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.plugin.annotation.DoMessage;
import cn.xeblog.plugin.enums.Style;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoMessage(MessageType.GAME_OVER)
public class GameOverMessageHandler extends AbstractGameMessageHandler<String> {

    @Override
    public void handle(Response<String> response) {
        GameAction.over();
        ConsoleAction.renderText(String.format("[%s] 系统消息：%s\n", response.getTime(),
                response.getBody()), Style.SYSTEM_MSG);
    }
}
