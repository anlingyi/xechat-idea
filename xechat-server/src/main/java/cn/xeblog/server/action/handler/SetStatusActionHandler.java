package cn.xeblog.server.action.handler;

import cn.xeblog.server.action.AbstractAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.UserStatus;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
public class SetStatusActionHandler extends AbstractAction<UserStatus> {

    @Override
    public void handle(ChannelHandlerContext ctx, UserStatus body) {
        User user = getUser(ctx);
        if (user.getStatus() == UserStatus.PLAYING) {
            ctx.channel().writeAndFlush(ResponseBuilder.system("正在游戏中，不能修改状态！"));
            return;
        }

        user.setStatus(body);
        ctx.channel().writeAndFlush(ResponseBuilder.system("状态修改成功！"));
    }

}
