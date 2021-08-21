package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.commons.enums.Action;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public class GameOverCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(String[] args) {
        if (checkOnline()) {
            String opponent = GameAction.getOpponent();
            if (opponent == null) {
                ConsoleAction.showSimpleMsg("结束个寂寞？");
                return;
            }

            MessageAction.send(DataCache.userMap.get(opponent), Action.GAME_OVER);
        }
    }

}
