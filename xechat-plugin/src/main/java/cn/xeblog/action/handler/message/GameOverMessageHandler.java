package cn.xeblog.action.handler.message;

import cn.xeblog.action.ConsoleAction;
import cn.xeblog.action.GameAction;
import cn.xeblog.entity.Response;
import cn.xeblog.enums.Style;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public class GameOverMessageHandler extends AbstractGameMessageHandler<String> {

    @Override
    public void handle(Response<String> response) {
        GameAction.over();
        ConsoleAction.renderText(String.format("[%s] 系统消息：%s\n", response.getTime(),
                response.getBody()), Style.SYSTEM_MSG);
    }
}
