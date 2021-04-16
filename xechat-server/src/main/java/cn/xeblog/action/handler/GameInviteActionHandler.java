package cn.xeblog.action.handler;

import cn.xeblog.builder.ResponseBuilder;
import cn.xeblog.entity.GameInviteDTO;
import cn.xeblog.entity.User;
import cn.xeblog.enums.MessageType;
import cn.xeblog.enums.UserStatus;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
public class GameInviteActionHandler extends AbstractGameActionHandler<GameInviteDTO> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameInviteDTO body) {
        User user = getUser(ctx);
        User inviteUser =  getUser(body.getId());

        if (opponentOffline(inviteUser, ctx)) {
            return;
        }

        if (inviteUser.getStatus() != UserStatus.FISHING) {
            ctx.channel().writeAndFlush(ResponseBuilder.system("人家正在" + inviteUser.getStatus().alias() + "呢！就你天天摸鱼？"));
            return;
        }

        user.setStatus(UserStatus.PLAYING);
        inviteUser.setStatus(UserStatus.PLAYING);
        inviteUser.getChannel().writeAndFlush(ResponseBuilder.build(user, body, MessageType.GAME_INVITE));
        ctx.channel().writeAndFlush(ResponseBuilder.system("已向" + inviteUser.getUsername() + "发送《" + body.getGame().getName() + "》游戏邀请！"));
    }
}
