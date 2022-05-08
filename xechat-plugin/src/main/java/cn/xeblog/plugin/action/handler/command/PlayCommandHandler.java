package cn.xeblog.plugin.action.handler.command;

import cn.hutool.core.util.NumberUtil;
import cn.xeblog.commons.entity.GameDTO;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.commons.entity.GameInviteResultDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.commons.enums.InviteStatus;
import cn.xeblog.plugin.enums.Command;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoCommand(Command.PLAY)
public class PlayCommandHandler extends AbstractCommandHandler {

    @Override
    public void process(String[] args) {
        if (GameAction.playing()) {
            ConsoleAction.showSimpleMsg("请先结束当前游戏！");
            return;
        }

        int len = args.length;
        if (len < 1) {
            ConsoleAction.showSimpleMsg("游戏编号不能为空！");
            return;
        }

        if (!NumberUtil.isNumber(args[0])) {
            ConsoleAction.showSimpleMsg("[" + args[0] + "]" + "不正确的游戏编号！");
            return;
        }

        Game game = Game.getGame(Integer.parseInt(args[0]));
        if (game == null) {
            ConsoleAction.showSimpleMsg("没有找到该游戏！");
            return;
        }

        boolean isOffline = !DataCache.isOnline;
        if (game.isRequiredLogin() && isOffline) {
            ConsoleAction.showLoginMsg();
            return;
        }

        if (GameAction.getOpponent() != null) {
            ConsoleAction.showSimpleMsg(GameAction.isProactive() ? "请等待【" + GameAction.getOpponent() + "】加入游戏！"
                    : "【" + GameAction.getOpponent() + "】已邀请你加入游戏，请确认！");
            return;
        }

        if (len < 2) {
            if (game.getMinPlayers() > 0) {
                ConsoleAction.showSimpleMsg("该游戏至少需要邀请" + game.getMinPlayers() + "人！");
                return;
            }

            String nickname = "玩家";
            if (!isOffline) {
                nickname = DataCache.username;
                MessageAction.send(new GameDTO(null, game), Action.GAME_INVITE);
            }

            GameAction.setOfflineGame(isOffline);
            GameAction.setNickname(nickname);
            GameAction.setGame(game);
            GameAction.create();
        } else {
            if (isOffline) {
                ConsoleAction.showLoginMsg();
                return;
            }

            if (DataCache.username.equals(args[1])) {
                ConsoleAction.showSimpleMsg("自娱自乐？？？");
                return;
            }

            String id = DataCache.userMap.get(args[1]);
            if (id == null) {
                ConsoleAction.showSimpleMsg("该用户不存在！");
                return;
            }

            MessageAction.send(new GameDTO(id, game), Action.GAME_INVITE);
            GameAction.setNickname(DataCache.username);
            GameAction.setOpponent(args[1]);
            GameAction.setGame(game);
            GameAction.setProactive(true);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int time = 30;

                @Override
                public void run() {
                    boolean timeout = --time < 0;
                    if (GameAction.playing() || GameAction.isOver() || timeout) {
                        timer.cancel();
                    }
                    if (timeout) {
                        GameInviteResultDTO result = new GameInviteResultDTO(InviteStatus.TIMEOUT);
                        result.setGame(GameAction.getGame());
                        result.setOpponentId(DataCache.userMap.get(GameAction.getOpponent()));
                        MessageAction.send(result, Action.GAME_INVITE_RESULT);
                    }
                }
            }, 0, 1000);
        }
    }

    @Override
    protected boolean check(String[] args) {
        return true;
    }

}
