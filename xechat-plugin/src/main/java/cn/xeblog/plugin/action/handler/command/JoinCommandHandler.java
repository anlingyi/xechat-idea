package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.commons.entity.GameRoomMsgDTO;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.commons.entity.GameInviteResultDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.InviteStatus;
import cn.xeblog.plugin.enums.Command;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoCommand(Command.JOIN)
public class JoinCommandHandler extends AbstractCommandHandler {

    @Override
    public void process(String[] args) {
        String roomId = GameAction.getRoomId();
        if (roomId == null) {
            ConsoleAction.showSimpleMsg("加入个寂寞？");
            return;
        }

        if (GameAction.isProactive()) {
            ConsoleAction.showSimpleMsg("你是游戏发起者！你加入个毛线？");
            return;
        }

        if (GameAction.playing()) {
            ConsoleAction.showSimpleMsg("请先结束当前游戏！");
            return;
        }

        GameRoomMsgDTO msg = new GameRoomMsgDTO();
        msg.setMsgType(GameRoomMsgDTO.MsgType.PLAYER_INVITE_RESULT);
        msg.setRoomId(roomId);
        GameInviteResultDTO gameInviteResultDTO = new GameInviteResultDTO(InviteStatus.ACCEPT);
        msg.setContent(gameInviteResultDTO);

        if (args.length > 0) {
            gameInviteResultDTO.setStatus(InviteStatus.REJECT);
            ConsoleAction.showSimpleMsg("你拒绝了" + GameAction.getInviter() + "的游戏邀请！");
            GameAction.clean();
        }

        MessageAction.send(msg, Action.GAME_ROOM);
    }

}
