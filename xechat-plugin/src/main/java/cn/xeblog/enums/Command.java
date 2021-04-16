package cn.xeblog.enums;

import cn.xeblog.action.ConsoleAction;
import cn.xeblog.action.handler.command.*;
import cn.xeblog.factory.CommandHandlerFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@Getter
@AllArgsConstructor
public enum Command implements CommandHandlerFactory {
    LOGIN("login", "登录，login {昵称}") {
        @Override
        public CommandHandler produce() {
            return new LoginCommandHandler();
        }
    },
    LOGOUT("logout", "退出") {
        @Override
        public CommandHandler produce() {
            return new LogoutCommandHandler();
        }
    },
    SHOW_STATUS("showStatus", "查看可用状态值") {
        @Override
        public CommandHandler produce() {
            return new ShowStatusCommandHandler();
        }
    },
    SET_STATUS("setStatus", "设置当前状态") {
        @Override
        public CommandHandler produce() {
            return new SetStatusCommandHandler();
        }
    },
    SHOW_GAME("showGame", "游戏列表") {
        @Override
        public CommandHandler produce() {
            return new ShowGameCommandHandler();
        }
    },
    PLAY("play", "游戏邀请，play {对方昵称} {游戏编号}") {
        @Override
        public CommandHandler produce() {
            return new PlayCommandHandler();
        }
    },
    JOIN("join", "加入游戏 | 拒绝邀请：此命令后加任意字符") {
        @Override
        public CommandHandler produce() {
            return new JoinCommandHandler();
        }
    },
    GAME_OVER("gameover", "结束游戏") {
        @Override
        public CommandHandler produce() {
            return new GameOverCommandHandler();
        }
    },
    MODE("mode", "模式设置，mode {模式编号}") {
        @Override
        public CommandHandler produce() {
            return new ModeCommandHandler();
        }
    },
    SHOW_MODE("showMode", "查看模式选项") {
        @Override
        public CommandHandler produce() {
            return new ShowModeCommandHandler();
        }
    },
    HELP("help", "帮助") {
        @Override
        public CommandHandler produce() {
            return new HelpCommandHandler();
        }
    }
    ;

    private String command;
    private String desc;

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
        produce().handle(args);
    }

    public String getCommand() {
        return COMMAND_PREFIX + command;
    }

}
