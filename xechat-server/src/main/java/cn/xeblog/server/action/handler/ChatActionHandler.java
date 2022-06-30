package cn.xeblog.server.action.handler;

import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.UserMsgDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.commons.enums.MessageType;
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
            body.setContent(SensitiveWordUtils.loveChina(body.getContent().toString()));
        }

        ChannelAction.send(user, body, MessageType.USER);
    }

}
