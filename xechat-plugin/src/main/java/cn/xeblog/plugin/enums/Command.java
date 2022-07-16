package cn.xeblog.plugin.enums;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.factory.CommandHandlerFactory;
import cn.xeblog.plugin.util.CommandHistoryUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@Getter
@AllArgsConstructor
public enum Command {
    LOGIN("login") {
        @Override
        public String getDesc() {
            return "登录，" + this.getCommand() + " {昵称} [-s {鱼塘编号} -h {服务端IP} -p {服务端端口} -c (清理缓存)]";
        }
    },
    SHOW_SERVER("showServer") {
        @Override
        public String getDesc() {
            return "鱼塘列表，" + this.getCommand() + " [-c（清理缓存)]";
        }
    },
    SHOW_STATUS("showStatus", "查看可用状态值"),
    SET_STATUS("setStatus") {
        @Override
        public String getDesc() {
            return "设置当前状态，" + this.getCommand() + " {状态值}";
        }
    },
    SHOW_GAME("showGame", "游戏列表"),
    PLAY("play") {
        @Override
        public String getDesc() {
            return "游戏功能，" + this.getCommand() + " {游戏编号}";
        }
    },
    JOIN("join", "加入游戏 | 拒绝邀请：此命令后加任意字符"),
    GAME_OVER("over", "结束游戏"),
    SHOW_MODE("showMode", "查看模式选项"),
    MODE("mode") {
        @Override
        public String getDesc() {
            return "模式设置，" + this.getCommand() + " {模式编号}";
        }
    },
    WEATHER("weather") {
        @Override
        public String getDesc() {
            return "天气查询，" + this.getCommand() + " {地名，如：北京市} [-d {0：当前，默认 | 3：未来3天 | 7：未来7天}]";
        }
    },
    NOTIFY("notify", "消息通知，1.正常通知 | 2.隐晦通知 | 3.关闭通知"),
    ALIVE("alive", "活着，0.关闭｜1.开启"),
    LOGOUT("exit", "退出"),
    CLEAN("clean", "清屏"),
    HELP("help", "帮助");

    private String command;
    private String desc;

    Command(String command) {
        this.command = command;
    }

    /**
     * 触发命令的前缀
     */
    public static final String COMMAND_PREFIX = "#";

    public static void handle(String command) {
        String[] strs = command.split(" ");
        if (strs.length > 0) {
            for (Command cmd : values()) {
                if (cmd.getCommand().equals(strs[0])) {
                    String[] args = null;
                    if (strs.length > 1) {
                        args = new String[strs.length - 1];
                        System.arraycopy(strs, 1, args, 0, args.length);
                    }

                    CommandHistoryUtils.addCommand(command);
                    cmd.exec(args);
                    return;
                }
            }
        }

        ConsoleAction.showErrorMsg();
    }

    public void exec(String[] args) {
        if (args == null) {
            args = new String[0];
        }

        CommandHandlerFactory.INSTANCE.produce(this).handle(args);
    }

    public String getCommand() {
        return COMMAND_PREFIX + command;
    }

}
