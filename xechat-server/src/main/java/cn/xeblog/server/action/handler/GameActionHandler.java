package cn.xeblog.server.action.handler;

import cn.xeblog.commons.enums.Action;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.commons.entity.GameDTO;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.MessageType;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.GAME)
public class GameActionHandler extends AbstractGameActionHandler<GameDTO> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameDTO body) {
        User gamer = getUser(body.getOpponentId());

        if (opponentOffline(gamer, ctx)) {
            return;
        }

        gamer.getChannel().writeAndFlush(ResponseBuilder.build(getUser(ctx), body, MessageType.GAME));
    }
}
