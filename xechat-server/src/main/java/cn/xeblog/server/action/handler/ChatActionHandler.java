package cn.xeblog.server.action.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Singleton;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.UserMsgDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.util.BaiDuFy;
import cn.xeblog.server.util.SensitiveWordUtils;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.CHAT)
public class ChatActionHandler extends AbstractActionHandler<UserMsgDTO> {

    @Override
    protected void process(User user, UserMsgDTO body) {
        if (body.getMsgType() == UserMsgDTO.MsgType.TEXT) {
            String msg = Convert.toStr(body.getContent());
            BaiDuFy baiDuFy = Singleton.get(BaiDuFy.class.getName(), () -> new BaiDuFy("", ""));
            body.setContent(baiDuFy.translate(SensitiveWordUtils.loveChina(msg)));
        }

        ChannelAction.send(user, body, MessageType.USER);
    }

}
