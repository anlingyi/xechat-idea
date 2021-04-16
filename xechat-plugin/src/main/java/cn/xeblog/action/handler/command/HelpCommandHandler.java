package cn.xeblog.action.handler.command;

import cn.xeblog.action.ConsoleAction;
import cn.xeblog.enums.Command;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
public class HelpCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("支持的命令（命令参数使用空格分开）\n");
        for (Command command : Command.values()) {
            sb.append("· ").append(command.getCommand()).append("：")
                    .append(command.getDesc()).append("\n");
        }
        ConsoleAction.showSimpleMsg(sb.toString());
    }

}
