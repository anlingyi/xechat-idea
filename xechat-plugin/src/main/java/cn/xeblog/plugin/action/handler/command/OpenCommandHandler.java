package cn.xeblog.plugin.action.handler.command;

import cn.hutool.core.util.NumberUtil;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.ToolAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.enums.Command;
import cn.xeblog.plugin.tools.Tools;

/**
 * @author anlingyi
 * @date 2022/8/5 5:34 上午
 */
@DoCommand(Command.OPEN)
public class OpenCommandHandler extends AbstractCommandHandler {

    @Override
    protected void process(String[] args) {
        int len = args.length;
        if (len < 1) {
            StringBuilder sb = new StringBuilder();
            Tools[] tools = Tools.values();
            for (int i = 0; i < tools.length; i++) {
                Tools tool = tools[i];
                sb.append(i).append(".").append(tool.getName()).append("\n");
            }

            ConsoleAction.showSimpleMsg(sb.toString());
            return;
        }

        if (!NumberUtil.isNumber(args[0])) {
            ConsoleAction.showSimpleMsg("[" + args[0] + "]" + "不正确的工具编号！");
            return;
        }

        Tools tools = Tools.getTool(Integer.parseInt(args[0]));
        if (tools == null) {
            ConsoleAction.showSimpleMsg("没有找到该工具！");
            return;
        }

        if (tools.isRequiredLogin() && !DataCache.isOnline) {
            ConsoleAction.showLoginMsg();
            return;
        }

        if (GameAction.playing()) {
            ConsoleAction.showSimpleMsg("请先退出游戏！");
            return;
        }

        if (ToolAction.getTools() == tools) {
            ConsoleAction.showSimpleMsg(tools.getName() + "当前已是开启状态!");
            return;
        }

        ToolAction.create(tools);

        ConsoleAction.showSimpleMsg(tools.getName() + "已开启！");
    }

    @Override
    protected boolean check(String[] args) {
        return true;
    }

}
