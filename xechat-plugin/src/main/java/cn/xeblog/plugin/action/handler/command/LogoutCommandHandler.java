package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.enums.Command;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoCommand(Command.LOGOUT)
public class LogoutCommandHandler extends AbstractCommandHandler {

    @Override
    public void process(String[] args) {
        if (!DataCache.isOnline) {
            ConsoleAction.showSimpleMsg("已是离线状态！");
            return;
        }

        if (GameAction.playing()) {
            // 结束游戏
            Command.GAME_OVER.exec(args);
        }

        ConsoleAction.showSimpleMsg("正在退出中...");
        DataCache.ctx.close();
    }

}
