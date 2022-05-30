package cn.xeblog.server.action.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.comparator.VersionComparator;
import cn.hutool.core.util.StrUtil;
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
import cn.xeblog.server.constant.CommonConstants;
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
        String currentPluginVersion = CommonConstants.PLUGIN_VERSION;
        String userPluginVersion = body.getPluginVersion();
        if (StrUtil.isNotBlank(userPluginVersion)) {
            int len = currentPluginVersion.length();
            int len2 = userPluginVersion.length();
            int k = len - len2;
            String padding = "";
            for (int i = Math.abs(k); i > 0; i--) {
                padding += "0";
            }
            if (k > 0) {
                userPluginVersion += padding;
            } else if (k < 0) {
                currentPluginVersion += padding;
            }
        }
        boolean needUpdate = VersionComparator.INSTANCE.compare(currentPluginVersion, userPluginVersion) > 0;
        if (needUpdate) {
            ctx.writeAndFlush(ResponseBuilder.system("温馨提醒~ 请尽快更新插件版本至 [v" + CommonConstants.PLUGIN_VERSION + "]！"));
            ctx.close();
            return;
        }

        boolean isReconnect = body.isReconnected();
        String username = body.getUsername();
        if (UserCache.existUsername(username)) {
            ctx.writeAndFlush(ResponseBuilder.system("[" + username + "]昵称重复！"));
            ctx.close();
            return;
        }

        String id = ChannelAction.getId(ctx);
        User user = new User(id, username, body.getStatus(), ctx.channel());
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
