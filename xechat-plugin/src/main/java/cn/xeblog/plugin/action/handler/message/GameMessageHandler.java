package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.commons.entity.GameDTO;
import cn.xeblog.commons.entity.Response;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public class GameMessageHandler extends AbstractGameMessageHandler<GameDTO> {

    @Override
    public void handle(Response<GameDTO> response) {
        GameAction.handle(response);
    }
}
