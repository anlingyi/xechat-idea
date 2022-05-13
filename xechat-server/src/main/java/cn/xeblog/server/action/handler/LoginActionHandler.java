package cn.xeblog.server.action.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.xeblog.commons.entity.HistoryMsgDTO;
import cn.xeblog.commons.entity.LoginDTO;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.cache.UserCache;
import cn.xeblog.commons.entity.User;
import cn.xeblog.server.factory.ObjectFactory;
import cn.xeblog.server.service.AbstractResponseHistoryService;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.LOGIN)
public class LoginActionHandler implements ActionHandler<LoginDTO> {

    @Override
    public void handle(ChannelHandlerContext ctx, LoginDTO body) {
        boolean isReconnect = body.isReconnected();
        String username = body.getUsername();
        if (UserCache.existUsername(username)) {
            ctx.writeAndFlush(ResponseBuilder.system("昵称重复！"));
            ctx.close();
            return;
        }

        User user = new User(username, body.getStatus(), ctx.channel());
        String id = ChannelAction.getId(ctx);
        UserCache.add(id, user);

        ChannelAction.sendOnlineUsers();
        if (isReconnect) {
            user.send(ResponseBuilder.system("重新连接服务器成功！"));
        }
        List<Response> historyMsgList = ObjectFactory.getObject(AbstractResponseHistoryService.class).getHistory(15);
        ChannelAction.send(ResponseBuilder.system(user.getUsername() + "进入了鱼塘！"));
        if (CollectionUtil.isNotEmpty(historyMsgList)) {
            user.send(ResponseBuilder.build(null, new HistoryMsgDTO(historyMsgList), MessageType.HISTORY_MSG));
        }
    }

}
