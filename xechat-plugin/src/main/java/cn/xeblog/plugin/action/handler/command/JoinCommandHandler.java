package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.builder.RequestBuilder;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.commons.entity.GameInviteResultDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.InviteStatus;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public class JoinCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(String[] args) {
        if (checkOnline()) {
            String opponent = GameAction.getOpponent();
            if (opponent == null) {
                ConsoleAction.showSimpleMsg("加入个寂寞？");
                return;
            }

            if (GameAction.isProactive()) {
                ConsoleAction.showSimpleMsg("你是游戏发起者！你加入个毛线？");
                return;
            }

            GameInviteResultDTO gameInviteResultDTO = new GameInviteResultDTO(InviteStatus.ACCEPT);
            gameInviteResultDTO.setGame(GameAction.getGame());
            gameInviteResultDTO.setOpponentId(DataCache.userMap.get(opponent));

            if (args.length > 0) {
                gameInviteResultDTO.setStatus(InviteStatus.REJECT);
                ConsoleAction.showSimpleMsg("你拒绝了" + opponent + "的游戏邀请！");
                GameAction.clean();
            }

            MessageAction.send(RequestBuilder.build(gameInviteResultDTO, Action.GAME_INVITE_RESULT));
        }
    }

}
