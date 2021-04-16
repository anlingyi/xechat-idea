package cn.xeblog.enums;

import cn.xeblog.action.ConsoleAction;

/**
 * @author anlingyi
 * @date 2020/6/1
 */
public enum UserStatus implements Status {
    WORKING {
        @Override
        public String alias() {
            return "工作中";
        }
    },
    FISHING {
        @Override
        public String alias() {
            return "摸鱼中";
        }
    },
    PLAYING {
        @Override
        public String alias() {
            return "游戏中";
        }
    }
    ;

    public static UserStatus getUserStatus(int index) {
        UserStatus[] userStatuses = values();
        if (index < 0 || index > userStatuses.length - 1) {
            return null;
        }

        UserStatus status = userStatuses[index];
        if (status == PLAYING) {
            // 暂时不能修改为游戏中状态
            return null;
        }

        return status;
    }

    public static void showUserStatusList() {
        StringBuilder sb = new StringBuilder();
        sb.append("状态值：");
        UserStatus[] userStatuses = values();
        for (int i = 0; i < userStatuses.length; i++) {
            if (userStatuses[i] == PLAYING) {
                // 暂时不能修改为游戏中状态
                continue;
            }

            sb.append(i).append(".").append(userStatuses[i].alias()).append(" ");
        }

        ConsoleAction.showSimpleMsg(sb.toString());
    }
}
