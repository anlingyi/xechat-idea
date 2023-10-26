package cn.xeblog.server.action.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.UserMsgDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.enums.Permissions;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.cache.UserCache;
import cn.xeblog.server.config.GlobalConfig;
import cn.xeblog.server.util.BaiDuFyUtil;
import cn.xeblog.server.util.SensitiveWordUtils;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.CHAT)
public class ChatActionHandler extends AbstractActionHandler<UserMsgDTO> {

    @Override
    protected void process(User user, UserMsgDTO body) {
        if (!user.hasPermit(Permissions.SPEAK)) {
            user.send(ResponseBuilder.system("您已被禁言！"));
            return;
        }
        if (!Permissions.SPEAK.hasPermit(GlobalConfig.GLOBAL_PERMIT)) {
            user.send(ResponseBuilder.system("鱼塘已开启全员禁言！"));
            return;
        }

        if (body.getMsgType() == UserMsgDTO.MsgType.TEXT) {
            String msg = Convert.toStr(body.getContent());
            if (StrUtil.length(msg) > 200) {
                user.send(ResponseBuilder.system("发送的内容长度不能超过200字符！"));
                return;
            }

            BaiDuFyUtil baiDuFyUtil = Singleton.get(BaiDuFyUtil.class.getName(), () -> new BaiDuFyUtil("", ""));
            body.setContent(baiDuFyUtil.translate(SensitiveWordUtils.loveChina(msg)));
            ChannelAction.send(user, body, MessageType.USER);
        } else if (body.getMsgType() == UserMsgDTO.MsgType.PRIVATE) {
            String toUserName = body.getToUsers()[0];
            String msg = Convert.toStr(body.getContent());

            // 消息原封不动发给自己 不然看不到自己发了啥
            user.send(ResponseBuilder.build(user, body, MessageType.USER));
            if (user.getUsername().equals(toUserName)) {
                user.send(ResponseBuilder.system("发给自己？是不是很无聊？"));
                return;
            }

            // 发给对方
            User toUser = UserCache.getUserByUsername(toUserName);
            if (toUser != null) {
                UserMsgDTO msgDTO = new UserMsgDTO(StrUtil.format("私聊消息::{}", msg), new String[]{toUserName});
                toUser.send(ResponseBuilder.build(user, msgDTO, MessageType.USER));
            } else {
                user.send(ResponseBuilder.system(StrUtil.format("用户[{}]不存在！", toUserName)));
            }

        } else {
            // 暂时不支持这种形式的消息，全部转为文本消息
            body.setMsgType(UserMsgDTO.MsgType.TEXT);
            ChannelAction.send(user, body, MessageType.USER);
        }
    }

}
