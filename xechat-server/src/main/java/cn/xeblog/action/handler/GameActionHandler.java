package cn.xeblog.action.handler;

import cn.xeblog.builder.ResponseBuilder;
import cn.xeblog.entity.GameDTO;
import cn.xeblog.entity.User;
import cn.xeblog.enums.MessageType;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
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
