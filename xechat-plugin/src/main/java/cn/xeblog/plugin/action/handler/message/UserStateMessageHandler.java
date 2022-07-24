package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.UserStateMsgDTO;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoMessage;
import cn.xeblog.plugin.cache.DataCache;

/**
 * @author anlingyi
 * @date 2022/7/23 3:37 PM
 */
@DoMessage(MessageType.USER_STATE)
public class UserStateMessageHandler extends AbstractMessageHandler<UserStateMsgDTO> {

    @Override
    protected void process(Response<UserStateMsgDTO> response) {
        UserStateMsgDTO dto = response.getBody();
        User user = dto.getUser();
        if (dto.getState() == UserStateMsgDTO.State.ONLINE) {
            DataCache.addUser(user);
        } else {
            DataCache.removeUser(user);
        }

        ConsoleAction.setConsoleTitle("Debug(" + DataCache.getOnlineUserTotal() + ")");
    }

}
