package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.AliveAction;
import cn.xeblog.plugin.action.ConsoleAction;

/**
 * @author anlingyi
 * @date 2021/9/4 8:16 下午
 */
public class AliveCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(String[] args) {
        if (args.length < 1) {
            return;
        }

        boolean enabled = Integer.parseInt(args[0]) > 0;
        AliveAction.setEnabled(enabled);
        ConsoleAction.showSimpleMsg("[活着]" + (enabled ? "已开启！" : "已关闭！"));
    }

}
