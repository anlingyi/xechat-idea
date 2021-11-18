package cn.xeblog.server.action.handler;

import cn.xeblog.commons.entity.HistoryMsgDTO;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.cache.UserCache;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.UserStatus;
import cn.xeblog.server.factory.ObjectFactory;
import cn.xeblog.server.service.AbstractResponseHistoryService;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.LOGIN)
public class LoginActionHandler implements ActionHandler<String> {

    @Override
    public void handle(ChannelHandlerContext ctx, String body) {
        String username = body;
        if (UserCache.existUsername(body)) {
            ctx.writeAndFlush(ResponseBuilder.system("昵称重复！"));
            ctx.close();
            return;
        }

        User user = new User(username, UserStatus.FISHING, ctx.channel());
        String id = ChannelAction.getId(ctx);
        UserCache.add(id, user);

        List<Response> historyMsgList = ObjectFactory.getObject(AbstractResponseHistoryService.class).getHistory();
        if (historyMsgList != null && historyMsgList.size() > 0) {
            ctx.writeAndFlush(ResponseBuilder.build(null, new HistoryMsgDTO(historyMsgList), MessageType.HISTORY_MSG));
        }

        ChannelAction.sendOnlineUsers();
        ChannelAction.send(ResponseBuilder.system(user.getUsername() + "进入了鱼塘！"));
    }

}
