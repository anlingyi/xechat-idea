package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.enums.Command;

/**
 * @author anlingyi
 * @date 2020/9/11
 */
@DoCommand(Command.SHOW_GAME)
public class ShowGameCommandHandler extends AbstractCommandHandler {

    @Override
    public void process(String[] args) {
        StringBuilder sb = new StringBuilder();
        Game[] games = Game.values();
        for (int i = 0; i < games.length; i++) {
            Game game = games[i];
            sb.append(i).append(".")
                    .append(game.getName())
                    .append("(可邀请").append(game.getMinPlayers());
            if (game.getMaxPlayers() > 0) {
                sb.append("~").append(game.getMaxPlayers());
            }
            sb.append("人)").append(" ");
        }

        ConsoleAction.showSimpleMsg(sb.toString());
    }

    @Override
    protected boolean check(String[] args) {
        return true;
    }
}
