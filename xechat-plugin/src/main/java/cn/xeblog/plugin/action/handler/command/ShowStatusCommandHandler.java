package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.commons.enums.UserStatus;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public class ShowStatusCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(String[] args) {
        if (checkOnline()) {
            StringBuilder sb = new StringBuilder();
            sb.append("状态值：");
            UserStatus[] userStatuses = UserStatus.values();
            for (int i = 0; i < userStatuses.length; i++) {
                if (userStatuses[i] == UserStatus.PLAYING) {
                    // 暂时不能修改为游戏中状态
                    continue;
                }

                sb.append(i).append(".").append(userStatuses[i].alias()).append(" ");
            }

            ConsoleAction.showSimpleMsg(sb.toString());
        }
    }

}
