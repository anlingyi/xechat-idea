package cn.xeblog.server.action.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.comparator.VersionComparator;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.constants.IpConstants;
import cn.xeblog.commons.entity.*;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.enums.Permissions;
import cn.xeblog.commons.enums.Platform;
import cn.xeblog.commons.util.CheckUtils;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.cache.UserCache;
import cn.xeblog.server.config.GlobalConfig;
import cn.xeblog.server.config.ServerConfig;
import cn.xeblog.server.constant.CommonConstants;
import cn.xeblog.server.factory.ObjectFactory;
import cn.xeblog.server.service.AbstractResponseHistoryService;
import cn.xeblog.server.util.IpUtil;
import cn.xeblog.server.util.SensitiveWordUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@Slf4j
@DoAction(Action.LOGIN)
public class LoginActionHandler implements ActionHandler<LoginDTO> {

    @Override
    public void handle(ChannelHandlerContext ctx, LoginDTO body) {
        if (ChannelAction.getUser(ctx) != null) {
            ctx.writeAndFlush(ResponseBuilder.system("请勿重复登录！"));
            return;
        }

        if (body.getPlatform() == null) {
            body.setPlatform(Platform.IDEA);
        }

        if (body.getPlatform() == Platform.IDEA) {
            // IDEA平台登录的需要比对插件版本
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

            int versionState = VersionComparator.INSTANCE.compare(currentPluginVersion, userPluginVersion);
            if (versionState > 0) {
                ctx.writeAndFlush(ResponseBuilder.system("温馨提醒~ 请尽快更新插件版本至v" + CommonConstants.PLUGIN_VERSION + "！"));
            }
            if (versionState < 0) {
                ctx.writeAndFlush(ResponseBuilder.system("当前服务端版本过低！你的版本：v" + body.getPluginVersion()
                        + "，服务端版本：v" + CommonConstants.PLUGIN_VERSION));
            }
        }

        boolean isReconnect = body.isReconnected();
        String username = body.getUsername();

        if (StrUtil.isBlank(username)) {
            ctx.writeAndFlush(ResponseBuilder.system("昵称不能为空！"));
            ctx.close();
            return;
        }

        if (!CheckUtils.checkUsername(username)) {
            ctx.writeAndFlush(ResponseBuilder.system("昵称不合法，请修改后重试！"));
            ctx.close();
            return;
        }

        if (username.length() > 12) {
            ctx.writeAndFlush(ResponseBuilder.system("昵称长度不能超过12个字符！"));
            ctx.close();
            return;
        }

        if (UserCache.existUsername(username)) {
            ctx.writeAndFlush(ResponseBuilder.system("[" + username + "]昵称重复！"));
            ctx.close();
            return;
        }

        if (SensitiveWordUtils.hasSensitiveWord(username)) {
            ctx.writeAndFlush(ResponseBuilder.system("昵称含有违规字符，请修改后重试！"));
            ctx.close();
            return;
        }

        if (StrUtil.isBlank(body.getUuid())) {
            ctx.writeAndFlush(ResponseBuilder.system("未获取到UUID，请尝试重新登录！"));
            ctx.close();
            return;
        }

        String id = ChannelAction.getId(ctx);
        final String ip = IpUtil.getIpByCtx(ctx);
        final IpRegion ipRegion = IpUtil.getRegionByIp(ip);
        String configToken = ServerConfig.getConfig().getToken();
        boolean isAdmin = StrUtil.isNotBlank(configToken) && StrUtil.equals(configToken, body.getToken());
        User user = new User(id, username, body.getStatus(), ip, ipRegion, ctx.channel());
        user.setUuid(body.getUuid());
        user.setRole(isAdmin ? User.Role.ADMIN : User.Role.USER);
        user.setPermit(GlobalConfig.getUserPermit(user));
        user.setPlatform(body.getPlatform());
        UserCache.add(id, user);

        ChannelAction.add(ctx.channel());
        ChannelAction.sendOnlineUsers(user);
        ChannelAction.sendUserState(user, UserStateMsgDTO.State.ONLINE);

        if (isReconnect) {
            user.send(ResponseBuilder.system("重新连接服务器成功！"));
        }
        user.send(ResponseBuilder.system("修身洁行，言必由绳墨。"));

        List<Response> historyMsgList = ObjectFactory.getObject(AbstractResponseHistoryService.class).getHistory(30);
        final String loginMsg = StrUtil.format("[{}·{}]进入了鱼塘！",
                MapUtil.getStr(IpConstants.SHORT_PROVINCE, ipRegion.getProvince(), ipRegion.getCountry()), user.getUsername());
        ChannelAction.send(ResponseBuilder.system(loginMsg));
        if (CollectionUtil.isNotEmpty(historyMsgList)) {
            user.send(ResponseBuilder.build(null, new HistoryMsgDTO(historyMsgList), MessageType.HISTORY_MSG));
        }
    }

}
