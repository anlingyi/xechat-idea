package cn.xeblog.server.action.handler;

import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.server.cache.UserCache;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.GAME_OVER)
public class GameOverActionHandler extends AbstractGameActionHandler<GameDTO> {

    @Override
    protected void process(User user, GameRoom gameRoom, GameDTO body) {
        Response resp = ResponseBuilder.build(user, body, MessageType.GAME_OVER);
        gameRoom.getUsers().forEach((k, v) -> {
            User player = UserCache.get(v.getId());
            if (player != null) {
                player.send(resp);
            }
        });
    }

}
