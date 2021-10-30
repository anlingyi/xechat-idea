package cn.xeblog.server.action.handler;

import cn.xeblog.commons.enums.Action;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.commons.entity.GameInviteDTO;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.enums.UserStatus;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.GAME_INVITE)
public class GameInviteActionHandler extends AbstractGameActionHandler<GameInviteDTO> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameInviteDTO body) {
        User user = getUser(ctx);
        user.setStatus(UserStatus.PLAYING);

        if (body.getId() != null) {
            User inviteUser = getUser(body.getId());
            if (opponentOffline(inviteUser, ctx)) {
                return;
            }

            if (inviteUser.getStatus() != UserStatus.FISHING) {
                ctx.channel().writeAndFlush(ResponseBuilder.system("人家正在" + inviteUser.getStatus().alias() + "呢！就你天天摸鱼？"));
                return;
            }

            inviteUser.setStatus(UserStatus.PLAYING);
            inviteUser.getChannel().writeAndFlush(ResponseBuilder.build(user, body, MessageType.GAME_INVITE));
            ctx.channel().writeAndFlush(ResponseBuilder.system("已向" + inviteUser.getUsername() + "发送《" + body.getGame().getName() + "》游戏邀请！"));
        }
    }
}
