package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.cache.DataCache;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public abstract class AbstractCommandHandler implements CommandHandler {

    protected static boolean checkOnline() {
        if (DataCache.isOnline) {
            return true;
        }

        ConsoleAction.showLoginMsg();
        return false;
    }
}
