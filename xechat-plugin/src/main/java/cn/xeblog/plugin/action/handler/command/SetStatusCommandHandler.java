package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.UserStatus;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.enums.Command;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoCommand(Command.SET_STATUS)
public class SetStatusCommandHandler extends AbstractCommandHandler {

    @Override
    public void process(String[] args) {
        if (DataCache.getCurrentUser().getStatus() == UserStatus.PLAYING) {
            ConsoleAction.showSimpleMsg("当前正在游戏中...不能设置状态！");
            return;
        }

        if (args.length < 1) {
            ConsoleAction.showSimpleMsg("状态值不能为空！");
            return;
        }

        int status = Integer.parseInt(args[0]);
        UserStatus userStatus = getUserStatus(status);
        if (userStatus == null) {
            ConsoleAction.showSimpleMsg("状态值不存在！");
            return;
        }

        MessageAction.send(userStatus, Action.SET_STATUS);
        DataCache.userStatus = userStatus;
        ConsoleAction.showSimpleMsg("[" + userStatus.alias() + "]状态设置成功！");
    }

    private static UserStatus getUserStatus(int index) {
        UserStatus[] userStatuses = UserStatus.values();
        if (index < 0 || index >= userStatuses.length) {
            return null;
        }

        UserStatus status = userStatuses[index];
        if (status == UserStatus.PLAYING) {
            // 暂时不能修改为游戏中状态
            return null;
        }

        return status;
    }

}
