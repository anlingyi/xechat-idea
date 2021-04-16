package cn.xeblog.action.handler.message;

import cn.xeblog.action.GameAction;
import cn.xeblog.entity.GameDTO;
import cn.xeblog.entity.Response;

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
