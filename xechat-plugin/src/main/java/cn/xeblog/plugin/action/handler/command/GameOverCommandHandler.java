package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.plugin.enums.Command;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoCommand(Command.GAME_OVER)
public class GameOverCommandHandler extends AbstractCommandHandler {

    @Override
    public void process(String[] args) {
        if (!GameAction.playing()) {
            ConsoleAction.showSimpleMsg("结束个寂寞？");
            return;
        }

        String opponent = GameAction.getOpponent();
        MessageAction.send(DataCache.userMap.get(opponent), Action.GAME_OVER);

        GameAction.over();
    }

}
