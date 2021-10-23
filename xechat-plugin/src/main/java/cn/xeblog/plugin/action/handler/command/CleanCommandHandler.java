package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.enums.Command;

/**
 * @author anlingyi
 * @date 2021/9/4 11:46 上午
 */
@DoCommand(Command.CLEAN)
public class CleanCommandHandler extends AbstractCommandHandler {

    @Override
    public void process(String[] args) {
        ConsoleAction.clean();
        ConsoleAction.showSimpleMsg("粉骨碎身浑不怕，要留清白在人间。");
    }

    @Override
    protected boolean check(String[] args) {
        return true;
    }

}
