package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.plugin.enums.Style;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public class UserMessageHandler extends AbstractMessageHandler<String> {

    @Override
    public void handle(Response<String> response) {
        User user = response.getUser();
        ConsoleAction.renderText(String.format("[%s] %s(%s)：", response.getTime(), user.getUsername(),
                user.getStatus().alias()), Style.USER_NAME);
        ConsoleAction.showSimpleMsg(response.getBody());
    }
}
