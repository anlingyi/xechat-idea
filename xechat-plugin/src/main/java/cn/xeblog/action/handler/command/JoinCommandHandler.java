package cn.xeblog.action.handler.command;

import cn.xeblog.action.ConsoleAction;
import cn.xeblog.action.GameAction;
import cn.xeblog.action.MessageAction;
import cn.xeblog.builder.RequestBuilder;
import cn.xeblog.entity.GameInviteResultDTO;
import cn.xeblog.enums.Action;
import cn.xeblog.enums.InviteStatus;

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

            if (args.length > 1) {
                gameInviteResultDTO.setStatus(InviteStatus.REJECT);
                ConsoleAction.showSimpleMsg("你拒绝了" + opponent + "的游戏邀请！");
                GameAction.clean();
            }

            MessageAction.send(RequestBuilder.build(gameInviteResultDTO, Action.GAME_INVITE_RESULT));
        }
    }

}
