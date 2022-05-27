package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.enums.UserStatus;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoMessage;
import cn.xeblog.plugin.cache.DataCache;

/**
 * @author anlingyi
 * @date 2022/5/26 4:45 下午
 */
@DoMessage(MessageType.STATUS_UPDATE)
public class StatusUpdateMessageHandler extends AbstractMessageHandler<UserStatus> {

    @Override
    protected void process(Response<UserStatus> response) {
        User user = DataCache.getUser(response.getUser().getUsername());
        if (user != null) {
            UserStatus userStatus = response.getBody();
            user.setStatus(userStatus);
        }
    }

}
