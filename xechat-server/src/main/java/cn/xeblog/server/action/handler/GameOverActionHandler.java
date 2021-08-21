package cn.xeblog.server.action.handler;

import cn.xeblog.server.action.AbstractAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.enums.UserStatus;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
public class GameOverActionHandler extends AbstractAction<String> {

    @Override
    public void handle(ChannelHandlerContext ctx, String body) {
        User user = getUser(ctx);
        user.setStatus(UserStatus.FISHING);
        Response resp = ResponseBuilder.build(user, user.getUsername() + "结束了游戏！", MessageType.GAME_OVER);
        ctx.channel().writeAndFlush(resp);

        if (body != null) {
            User op =  getUser(body);
            if (op != null) {
                op.setStatus(UserStatus.FISHING);
                op.getChannel().writeAndFlush(resp);
            }
        }
    }

}
