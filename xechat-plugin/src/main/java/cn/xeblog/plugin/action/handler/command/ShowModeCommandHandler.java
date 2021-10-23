package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.enums.Command;
import cn.xeblog.plugin.mode.ModeContext;
import cn.xeblog.plugin.mode.ModeEnum;

/**
 * @author anlingyi
 * @date 2020/9/1
 */
@DoCommand(Command.SHOW_MODE)
public class ShowModeCommandHandler extends AbstractCommandHandler {

    @Override
    public void process(String[] args) {
        ModeEnum[] modes = ModeEnum.values();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < modes.length; i++) {
            sb.append(i).append(".").append(modes[i].getName()).append(hasBeenSet(modes[i])).append(" ");
        }
        ConsoleAction.showSimpleMsg(sb.toString());
    }

    private static String hasBeenSet(ModeEnum modeEnum) {
        return ModeContext.getMode() == modeEnum ? "（已设置）" : "";
    }

    @Override
    protected boolean check(String[] args) {
        return true;
    }
}
