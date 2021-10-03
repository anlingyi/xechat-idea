package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.enums.Command;
import cn.xeblog.plugin.util.IdeaUtils;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoCommand(Command.HELP)
public class HelpCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("支持的命令（命令参数使用空格分开）\n");
        for (Command command : Command.values()) {
            sb.append("· ").append(command.getCommand()).append("：")
                    .append(command.getDesc()).append("\n");
        }
        sb.append("[当前版本：v").append(IdeaUtils.getPluginVersion()).append("]");
        ConsoleAction.showSimpleMsg(sb.toString());
    }

}
