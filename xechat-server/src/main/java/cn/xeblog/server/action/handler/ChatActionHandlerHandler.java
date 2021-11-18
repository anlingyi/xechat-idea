package cn.xeblog.server.action.handler;

import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.commons.enums.MessageType;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.CHAT)
public class ChatActionHandlerHandler extends AbstractActionHandler<String> {

    @Override
    protected void process(User user, String body) {
        ChannelAction.send(user, body, MessageType.USER);
    }

}
