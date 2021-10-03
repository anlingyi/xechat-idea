package cn.xeblog.server.action.handler;

import cn.xeblog.commons.enums.Action;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.commons.entity.GameInviteResultDTO;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.InviteStatus;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.enums.UserStatus;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.GAME_INVITE_RESULT)
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
