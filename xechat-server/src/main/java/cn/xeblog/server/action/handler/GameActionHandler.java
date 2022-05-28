package cn.xeblog.server.action.handler;

import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.server.cache.UserCache;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.GAME)
public class GameActionHandler extends AbstractGameActionHandler<GameDTO> {

    @Override
    protected void process(User user, GameRoom gameRoom, GameDTO body) {
        gameRoom.getUsers().forEach((k, v) -> {
            if (v.getId().equals(user.getId())) {
                return;
            }

            User player = UserCache.get(v.getId());
            if (player == null) {
                return;
            }

            player.send(ResponseBuilder.build(user, body, MessageType.GAME));
        });
    }

}
