package cn.xeblog.action.handler.command;

import cn.xeblog.action.ConsoleAction;
import cn.xeblog.cache.DataCache;
import cn.xeblog.client.XEChatClient;
import org.apache.commons.lang3.StringUtils;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public class LoginCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(String[] args) {
        if (DataCache.isOnline) {
            ConsoleAction.showSimpleMsg("已是登录状态！");
            return;
        }
        if (args.length < 2) {
            ConsoleAction.showSimpleMsg("用户名不能为空！");
            return;
        }

        String username = args[1];
        if (StringUtils.isBlank(username)) {
            ConsoleAction.showSimpleMsg("用户名不能为空！");
            return;
        }
        if (username.length() > 12) {
            ConsoleAction.showSimpleMsg("用户名长度不能超过12个字符！");
            return;
        }

        DataCache.username = username;
        ConsoleAction.showSimpleMsg("正在登录中...");
        new Thread(() -> {
            XEChatClient.run();
        }).start();
    }

}
