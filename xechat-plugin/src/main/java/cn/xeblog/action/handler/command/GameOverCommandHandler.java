package cn.xeblog.action.handler.command;

import cn.xeblog.action.ConsoleAction;
import cn.xeblog.action.GameAction;
import cn.xeblog.action.MessageAction;
import cn.xeblog.builder.RequestBuilder;
import cn.xeblog.cache.DataCache;
import cn.xeblog.enums.Action;

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

            MessageAction.send(RequestBuilder.build(DataCache.userMap.get(opponent), Action.GAME_OVER));
        }
    }

}
