package cn.xeblog.server.action.handler;

import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.UserStatus;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.SET_STATUS)
public class SetStatusActionHandler extends AbstractActionHandler<UserStatus> {

    @Override
    protected void process(User user, UserStatus body) {
        user.setStatus(body);
        ChannelAction.send(ResponseBuilder.build(user, body, MessageType.STATUS_UPDATE));
    }

}
