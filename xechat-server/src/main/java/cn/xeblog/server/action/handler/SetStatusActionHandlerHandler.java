package cn.xeblog.server.action.handler;

import cn.xeblog.commons.enums.Action;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.UserStatus;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@DoAction(Action.SET_STATUS)
public class SetStatusActionHandlerHandler extends AbstractActionHandler<UserStatus> {

    @Override
    protected void process(User user, UserStatus body) {
        if (user.getStatus() == UserStatus.PLAYING) {
            user.send(ResponseBuilder.system("正在游戏中，不能修改状态！"));
            return;
        }

        user.setStatus(body);
        user.send(ResponseBuilder.system("状态修改成功！"));
    }

}
