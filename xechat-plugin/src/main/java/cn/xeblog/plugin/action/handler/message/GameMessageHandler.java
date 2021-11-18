package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.commons.entity.GameDTO;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.plugin.annotation.DoMessage;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoMessage(MessageType.GAME)
public class GameMessageHandler extends AbstractGameMessageHandler<GameDTO> {

    @Override
    protected void process(Response<GameDTO> response) {
        GameAction.handle(response);
    }

}
