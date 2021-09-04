package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConsoleAction;

/**
 * @author anlingyi
 * @date 2021/9/4 11:46 上午
 */
public class CleanCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(String[] args) {
        ConsoleAction.clean();
        ConsoleAction.showSimpleMsg("粉骨碎身浑不怕，要留清白在人间。");
    }

}
