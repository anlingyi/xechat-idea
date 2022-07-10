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
    LOGIN("login", "登录，login {昵称} [-h {服务端ip，可选}] [-p {服务端端口，可选}]"),
    LOGOUT("exit", "退出"),
    SHOW_STATUS("showStatus", "查看可用状态值"),
    SET_STATUS("setStatus", "设置当前状态，setStatus {状态值}"),
    SHOW_GAME("showGame", "游戏列表"),
    PLAY("play", "游戏功能，play {游戏编号}"),
    JOIN("join", "加入游戏 | 拒绝邀请：此命令后加任意字符"),
    GAME_OVER("over", "结束游戏"),
    MODE("mode", "模式设置，mode {模式编号}"),
    SHOW_MODE("showMode", "查看模式选项"),
    CLEAN("clean", "清屏"),
    ALIVE("alive", "活着，0.关闭｜1.开启"),
    NOTIFY("notify", "消息通知，1.正常通知 | 2.隐晦通知 | 3.关闭通知"),
    WEATHER("weather", "天气查询，weather {地名，如：北京市} [-d {0：当前，默认 | 3：未来3天 | 7：未来7天}]"),
    HELP("help", "帮助");

    private String command;
    private String desc;

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
