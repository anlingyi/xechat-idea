package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.builder.RequestBuilder;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.UserStatus;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public class SetStatusCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(String[] args) {
        if (checkOnline()) {
            if (args.length < 2) {
                ConsoleAction.showSimpleMsg("状态值不能为空！");
                return;
            }

            int status = Integer.parseInt(args[1]);
            UserStatus userStatus = getUserStatus(status);
            if (userStatus == null) {
                ConsoleAction.showSimpleMsg("状态值不存在");
                return;
            }

            MessageAction.send(RequestBuilder.build(userStatus, Action.SET_STATUS));
        }
    }

    private static UserStatus getUserStatus(int index) {
        UserStatus[] userStatuses = UserStatus.values();
        if (index < 0 || index > userStatuses.length - 1) {
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
