package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.commons.entity.GameDTO;
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

        String opponentId = null;
        String opponent = GameAction.getOpponent();
        if (opponent != null) {
            opponentId = DataCache.userMap.get(opponent);
        }
        MessageAction.send(new GameDTO(opponentId, GameAction.getGame()), Action.GAME_OVER);

        GameAction.over();
    }

}
