package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConnectionAction;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.cache.DataCache;
import org.apache.commons.lang3.StringUtils;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public class LoginCommandHandler extends AbstractCommandHandler {

    private String[] args;

    @Override
    public void handle(String[] args) {
        if (DataCache.isOnline) {
            ConsoleAction.showSimpleMsg("已是登录状态！");
            return;
        }

        this.args = args;
        int len = args.length;
        if (len < 2) {
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

        ConnectionAction conn = new ConnectionAction();
        if (len > 3) {
            setConnection(2, conn);
        }
        if (len > 5) {
            setConnection(4, conn);
        }

        DataCache.username = username;
        ConsoleAction.showSimpleMsg("正在登录中...");
        conn.exec();
    }

    private void setConnection(int index, ConnectionAction conn) {
        int k = index + 1;
        switch (StringUtils.lowerCase(this.args[index])) {
            case "-h":
                conn.setHost(this.args[k]);
                return;
            case "-p":
                conn.setPort(Integer.parseInt(this.args[k]));
                return;
        }
    }

}
