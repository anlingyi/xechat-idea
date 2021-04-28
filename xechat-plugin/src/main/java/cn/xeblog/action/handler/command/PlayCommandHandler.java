package cn.xeblog.action.handler.command;

import cn.xeblog.action.ConsoleAction;
import cn.xeblog.action.GameAction;
import cn.xeblog.action.MessageAction;
import cn.xeblog.builder.RequestBuilder;
import cn.xeblog.cache.DataCache;
import cn.xeblog.entity.GameInviteDTO;
import cn.xeblog.entity.GameInviteResultDTO;
import cn.xeblog.enums.Action;
import cn.xeblog.enums.Game;
import cn.xeblog.enums.InviteStatus;

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
            if (len < 2) {
                ConsoleAction.showSimpleMsg("用户名不能为空！");
                return;
            }

            if (DataCache.username.equals(args[1])) {
                ConsoleAction.showSimpleMsg("自娱自乐？？？");
                return;
            }

            if (GameAction.getOpponent() != null) {
                ConsoleAction.showSimpleMsg("吃着碗里的看着锅里的？");
                return;
            }

            String id = DataCache.userMap.get(args[1]);
            if (id == null) {
                ConsoleAction.showSimpleMsg("该用户不存在！");
                return;
            }

            Game game = null;
            if (len > 2) {
                game = Game.getGame(Integer.parseInt(args[2]));
            }
            if (game == null) {
                game = Game.GOBANG;
            }

            MessageAction.send(RequestBuilder.build(new GameInviteDTO(id, game), Action.GAME_INVITE));
            GameAction.setOpponent(args[1]);
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
