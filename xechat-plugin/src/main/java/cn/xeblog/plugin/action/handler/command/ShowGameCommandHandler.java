package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.action.ConsoleAction;

/**
 * @author anlingyi
 * @date 2020/9/11
 */
public class ShowGameCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(String[] args) {
        StringBuilder sb = new StringBuilder();
        Game[] games = Game.values();
        for (int i = 0; i < games.length; i++) {
            sb.append(i).append(".").append(games[i].getName()).append(" ");
        }

        ConsoleAction.showSimpleMsg(sb.toString());
    }

}
