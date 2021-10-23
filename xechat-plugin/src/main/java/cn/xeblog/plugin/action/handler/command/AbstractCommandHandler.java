package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.cache.DataCache;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public abstract class AbstractCommandHandler implements CommandHandler {

    protected boolean check(String[] args) {
        if (DataCache.isOnline) {
            return true;
        }

        ConsoleAction.showLoginMsg();
        return false;
    }

    @Override
    public void handle(String[] args) {
        if (!check(args)) {
            return;
        }

        process(args);
    }

    protected abstract void process(String[] args);

}
