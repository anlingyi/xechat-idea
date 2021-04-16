package cn.xeblog.action.handler.command;

import cn.xeblog.action.ConsoleAction;
import cn.xeblog.action.MessageAction;
import cn.xeblog.builder.RequestBuilder;
import cn.xeblog.enums.Action;
import cn.xeblog.enums.UserStatus;

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
            UserStatus userStatus = UserStatus.getUserStatus(status);
            if (userStatus == null) {
                ConsoleAction.showSimpleMsg("状态值不存在");
                return;
            }

            MessageAction.send(RequestBuilder.build(userStatus, Action.SET_STATUS));
        }
    }

}
