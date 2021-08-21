package cn.xeblog.plugin.enums;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.handler.command.*;
import cn.xeblog.plugin.factory.CommandHandlerFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@Getter
@AllArgsConstructor
public enum Command {
    LOGIN("login", "登录，login {昵称}", LoginCommandHandler.class),
    LOGOUT("logout", "退出", LogoutCommandHandler.class),
    SHOW_STATUS("showStatus", "查看可用状态值", ShowStatusCommandHandler.class),
    SET_STATUS("setStatus", "设置当前状态", SetStatusCommandHandler.class),
    SHOW_GAME("showGame", "游戏列表", ShowGameCommandHandler.class),
    PLAY("play", "游戏邀请，play {对方昵称} {游戏编号}", PlayCommandHandler.class),
    JOIN("join", "加入游戏 | 拒绝邀请：此命令后加任意字符", JoinCommandHandler.class),
    GAME_OVER("gameover", "结束游戏", GameOverCommandHandler.class),
    MODE("mode", "模式设置，mode {模式编号}", ModeCommandHandler.class),
    SHOW_MODE("showMode", "查看模式选项", ShowModeCommandHandler.class),
    HELP("help", "帮助", HelpCommandHandler.class);

    private String command;
    private String desc;
    private Class handlerClass;

    /**
     * 触发命令的前缀
     */
    public static final String COMMAND_PREFIX = "#";

    public static void handle(String command) {
        String[] args = command.split(" ");
        if (args.length > 0) {
            for (Command cmd : values()) {
                if (cmd.getCommand().equals(args[0])) {
                    cmd.exec(args);
                    return;
                }
            }
        }

        ConsoleAction.showErrorMsg();
    }

    public void exec(String[] args) {
        CommandHandlerFactory.INSTANCE.produce(this).handle(args);
    }

    public String getCommand() {
        return COMMAND_PREFIX + command;
    }

}
