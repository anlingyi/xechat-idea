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
    public void process(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append(" 命令列表 & 触发命令前缀 \"" + Command.COMMAND_PREFIX + "\"\n");
        for (Command command : Command.values()) {
            sb.append("· ").append(command.getCommand()).append("：")
                    .append(command.getDesc()).append("\n");
        }
        sb.append("\n Version ").append(IdeaUtils.getPluginVersion());
        ConsoleAction.showSimpleMsg(sb.toString());
        ConsoleAction.renderText("--------------\n ");
        ConsoleAction.renderUrl("[开源]", "https://github.com/anlingyi/xechat-idea");
        ConsoleAction.renderText("  ");
        ConsoleAction.renderUrl("[更多]", "https://xeblog.cn/?tag=xechat-idea");
        ConsoleAction.renderText("\n--------------\n");
    }

    @Override
    protected boolean check(String[] args) {
        return true;
    }
}
