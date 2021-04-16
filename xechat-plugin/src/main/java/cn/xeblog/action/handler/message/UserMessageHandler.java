package cn.xeblog.action.handler.message;

import cn.xeblog.action.ConsoleAction;
import cn.xeblog.entity.Response;
import cn.xeblog.entity.User;
import cn.xeblog.enums.Style;

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
