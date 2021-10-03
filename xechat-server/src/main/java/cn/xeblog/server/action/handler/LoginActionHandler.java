package cn.xeblog.server.action.handler;

import cn.xeblog.commons.entity.HistoryMsgDTO;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.server.action.AbstractAction;
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
public class LoginActionHandler extends AbstractAction<String> {

    @Override
    public void handle(ChannelHandlerContext ctx, String body) {
        String username = body;
        String id = getId(ctx);
        if (UserCache.existUsername(body)) {
            username += id;
        }

        User user = new User(username, UserStatus.FISHING, ctx.channel());
        UserCache.add(id, user);

        List<Response> historyMsgList = ObjectFactory.getObject(AbstractResponseHistoryService.class).getHistory();
        if (historyMsgList != null && historyMsgList.size() > 0) {
            ctx.writeAndFlush(ResponseBuilder.build(null, new HistoryMsgDTO(historyMsgList), MessageType.HISTORY_MSG));
        }

        sendOnlineUsers();
        writeAndFlush(ResponseBuilder.system(user.getUsername() + "进入了鱼塘！"));
    }

}
