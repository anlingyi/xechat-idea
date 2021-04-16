package cn.xeblog.action.handler.command;

import cn.xeblog.action.ConsoleAction;
import cn.xeblog.action.GameAction;
import cn.xeblog.cache.DataCache;
import cn.xeblog.enums.Command;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public class LogoutCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(String[] args) {
        if (!DataCache.isOnline) {
            ConsoleAction.showSimpleMsg("已是离线状态！");
            return;
        }

        if (GameAction.getOpponent() != null) {
            // 结束游戏
            Command.GAME_OVER.exec(args);
            GameAction.over();
        }

        ConsoleAction.showSimpleMsg("正在退出中...");
        DataCache.ctx.close();
    }

}
