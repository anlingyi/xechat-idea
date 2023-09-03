package cn.xeblog.server.action.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Singleton;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.UserMsgDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.enums.Permissions;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
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
            BaiDuFyUtil baiDuFyUtil = Singleton.get(BaiDuFyUtil.class.getName(), () -> new BaiDuFyUtil("", ""));
            body.setContent(baiDuFyUtil.translate(SensitiveWordUtils.loveChina(msg)));
        } else {
            // 暂时不支持这种形式的消息，全部转为文本消息
            body.setMsgType(UserMsgDTO.MsgType.TEXT);
        }

        ChannelAction.send(user, body, MessageType.USER);
    }

}
