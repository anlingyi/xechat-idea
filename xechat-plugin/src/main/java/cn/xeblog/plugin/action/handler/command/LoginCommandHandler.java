package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConnectionAction;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.enums.Command;
import org.apache.commons.lang3.StringUtils;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoCommand(Command.LOGIN)
public class LoginCommandHandler extends AbstractCommandHandler {

    private String[] args;

    @Override
    public void process(String[] args) {
        if (DataCache.isOnline) {
            ConsoleAction.showSimpleMsg("已是登录状态！");
            return;
        }

        this.args = args;
        int len = args.length;
        if (len < 1) {
            ConsoleAction.showSimpleMsg("用户名不能为空！");
            return;
        }

        String username = args[0];

        if (StringUtils.isBlank(username)) {
            ConsoleAction.showSimpleMsg("用户名不能为空！");
            return;
        }
        if (username.length() > 12) {
            ConsoleAction.showSimpleMsg("用户名长度不能超过12个字符！");
            return;
        }

        ConnectionAction conn = new ConnectionAction();
        if (len > 2) {
            setConnection(1, conn);
        }
        if (len > 4) {
            setConnection(3, conn);
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

    @Override
    protected boolean check(String[] args) {
        return true;
    }
}
