package cn.xeblog.plugin.action.handler.command;

import cn.hutool.core.util.NumberUtil;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.UserStatus;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.enums.Command;

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

        if (GameAction.getInviter() != null) {
            ConsoleAction.showSimpleMsg(GameAction.isProactive() ? "请等待【" + GameAction.getInviter() + "】加入游戏！"
                    : "【" + GameAction.getInviter() + "】已邀请你加入游戏，请确认！");
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

        boolean isOnline = DataCache.isOnline;
        if (game.isRequiredLogin() && !isOnline) {
            ConsoleAction.showLoginMsg();
            return;
        }

        String nickname = "玩家";
        if (isOnline) {
            nickname = DataCache.username;
        }

        GameAction.setNickname(nickname);
        GameAction.setGame(game);
        GameAction.create();
        if (isOnline) {
            MessageAction.send(UserStatus.PLAYING, Action.SET_STATUS);
        }

        ConsoleAction.showSimpleMsg(game.getName() + "游戏开始！");
    }

    @Override
    protected boolean check(String[] args) {
        return true;
    }

}
