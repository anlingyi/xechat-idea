package cn.xeblog.action.handler.command;

import cn.xeblog.enums.UserStatus;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public class ShowStatusCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(String[] args) {
        if (checkOnline()) {
            UserStatus.showUserStatusList();
        }
    }

}
