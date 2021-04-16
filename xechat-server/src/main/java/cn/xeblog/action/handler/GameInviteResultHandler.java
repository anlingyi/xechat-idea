package cn.xeblog.action.handler;

import cn.xeblog.builder.ResponseBuilder;
import cn.xeblog.entity.GameInviteResultDTO;
import cn.xeblog.entity.Response;
import cn.xeblog.entity.User;
import cn.xeblog.enums.InviteStatus;
import cn.xeblog.enums.MessageType;
import cn.xeblog.enums.UserStatus;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
public class GameInviteResultHandler extends AbstractGameActionHandler<GameInviteResultDTO> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameInviteResultDTO body) {
        User user = getUser(ctx);
        User opponent = getUser(body.getOpponentId());

        if (opponentOffline(opponent, ctx)) {
            return;
        }

        if (body.getStatus() != InviteStatus.ACCEPT) {
            user.setStatus(UserStatus.FISHING);
            opponent.setStatus(UserStatus.FISHING);
        }

        Response response = ResponseBuilder.build(user, body, MessageType.GAME_INVITE_RESULT);
        if (body.getStatus() != InviteStatus.REJECT) {
            ctx.channel().writeAndFlush(response);
        }

        opponent.getChannel().writeAndFlush(response);
    }

}
