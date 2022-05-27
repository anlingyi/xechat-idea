package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.entity.GameDTO;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.plugin.annotation.DoMessage;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoMessage(MessageType.GAME_OVER)
public class GameOverMessageHandler extends AbstractGameMessageHandler<GameDTO> {

    @Override
    protected void process(Response<GameDTO> response) {
        ConsoleAction.showSystemMsg(response.getTime(), response.getUser().getUsername() + "结束了游戏！");
    }

}
