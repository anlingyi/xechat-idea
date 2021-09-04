package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.builder.RequestBuilder;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.commons.entity.GameInviteDTO;
import cn.xeblog.commons.entity.GameInviteResultDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.commons.enums.InviteStatus;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public class PlayCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(String[] args) {
        int len = args.length;
        if (checkOnline()) {
            if (len < 1) {
                ConsoleAction.showSimpleMsg("用户名不能为空！");
                return;
            }

            if (DataCache.username.equals(args[0])) {
                ConsoleAction.showSimpleMsg("自娱自乐？？？");
                return;
            }

            if (GameAction.getOpponent() != null) {
                ConsoleAction.showSimpleMsg("吃着碗里的看着锅里的？");
                return;
            }

            String id = DataCache.userMap.get(args[0]);
            if (id == null) {
                ConsoleAction.showSimpleMsg("该用户不存在！");
                return;
            }

            Game game = null;
            if (len > 1) {
                game = Game.getGame(Integer.parseInt(args[1]));
            }
            if (game == null) {
                game = Game.GOBANG;
            }

            MessageAction.send(RequestBuilder.build(new GameInviteDTO(id, game), Action.GAME_INVITE));
            GameAction.setOpponent(args[0]);
            GameAction.setProactive(true);
            GameAction.setGame(game);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int time = 0;
                @Override
                public void run() {
                    boolean timeout = ++time > 29;
                    if (GameAction.playing() || timeout) {
                        timer.cancel();
                    }
                    if (timeout) {
                        GameInviteResultDTO result = new GameInviteResultDTO(InviteStatus.TIMEOUT);
                        result.setGame(GameAction.getGame());
                        result.setOpponentId(DataCache.userMap.get(GameAction.getOpponent()));
                        MessageAction.send(RequestBuilder.build(result, Action.GAME_INVITE_RESULT));
                    }
                }
            }, 0, 1000);
        }
    }

}
