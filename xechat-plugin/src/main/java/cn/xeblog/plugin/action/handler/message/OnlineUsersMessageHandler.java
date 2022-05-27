package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.UserListMsgDTO;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoMessage;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.commons.entity.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoMessage(MessageType.ONLINE_USERS)
public class OnlineUsersMessageHandler extends AbstractMessageHandler<UserListMsgDTO> {

    @Override
    protected void process(Response<UserListMsgDTO> response) {
        Map<String, User> userMap = new ConcurrentHashMap<>();
        List<User> userList = response.getBody().getUserList();
        userList.forEach(user -> userMap.put(user.getUsername(), user));
        DataCache.userMap = userMap;
        ConsoleAction.setConsoleTitle("Debug(" + userList.size() + ")");
    }

}
