package cn.xeblog.plugin.action.handler.command;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.plugin.action.ConnectionAction;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.enums.Command;
import cn.xeblog.commons.util.ParamsUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoCommand(Command.LOGIN)
public class LoginCommandHandler extends AbstractCommandHandler {

    private static boolean CONNECTING;

    @Getter
    @AllArgsConstructor
    private enum Config {
        /**
         * 服务器地址
         */
        HOST("-h"),
        /**
         * 端口
         */
        PORT("-p"),
        /**
         * 清除缓存的服务器配置信息
         */
        CLEAN("-c");

        private String key;

        public static Config getConfig(String name) {
            for (Config value : values()) {
                if (value.getKey().equals(name)) {
                    return value;
                }
            }

            return null;
        }
    }

    @Override
    public void process(String[] args) {
        if (DataCache.isOnline) {
            ConsoleAction.showSimpleMsg("已是登录状态！");
            return;
        }
        if (CONNECTING) {
            ConsoleAction.showSimpleMsg("请等待之前的连接...");
            return;
        }

        int len = args.length;
        String username = DataCache.username;
        if (len > 0) {
            String name = args[0];
            if (Config.getConfig(name) == null) {
                username = name;
            }
        }
        username = StrUtil.trim(username);

        if (StringUtils.isBlank(username)) {
            ConsoleAction.showSimpleMsg("用户名不能为空！");
            return;
        }
        if (username.length() > 12) {
            ConsoleAction.showSimpleMsg("用户名长度不能超过12个字符！");
            return;
        }

        if (ParamsUtils.hasKey(args, Config.CLEAN.getKey())) {
            DataCache.connectionAction = null;
        }

        ConnectionAction conn = new ConnectionAction();
        if (DataCache.connectionAction != null) {
            BeanUtil.copyProperties(DataCache.connectionAction, conn);
        }

        String host = ParamsUtils.getValue(args, Config.HOST.getKey());
        String port = ParamsUtils.getValue(args, Config.PORT.getKey());
        if (StrUtil.isNotBlank(host)) {
            conn.setHost(host);
        }
        if (StrUtil.isNotBlank(port)) {
            conn.setPort(Integer.parseInt(port));
        }

        CONNECTING = true;
        DataCache.username = username;
        ConsoleAction.showSimpleMsg("正在连接服务器...");
        conn.exec((flag) -> {
            CONNECTING = false;
            if (flag) {
                DataCache.connectionAction = conn;
            }
        });
    }

    @Override
    protected boolean check(String[] args) {
        return true;
    }
}
