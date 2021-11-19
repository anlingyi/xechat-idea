package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoMessage;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.commons.entity.Response;

import java.util.Map;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoMessage(MessageType.ONLINE_USERS)
public class OnlineUsersMessageHandler extends AbstractMessageHandler<Map<String, String>> {

    @Override
    protected void process(Response<Map<String, String>> response) {
        DataCache.userMap = response.getBody();
        ConsoleAction.setConsoleTitle("Debug(" + DataCache.userMap.size() + ")");
    }

}
