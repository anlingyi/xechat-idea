package cn.xeblog.action.handler.command;

import cn.xeblog.action.ConsoleAction;
import cn.xeblog.mode.ModeContext;
import cn.xeblog.mode.ModeEnum;

/**
 * @author anlingyi
 * @date 2020/9/1
 */
public class ModeCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(String[] args) {
        if (args.length < 2) {
            return;
        }

        ModeEnum mode = ModeEnum.getMode(Integer.parseInt(args[1]));
        if (mode == null) {
            return;
        }

        ModeContext.setMode(mode);
        ConsoleAction.showSimpleMsg("【" + mode.getName() + "】模式设置成功！");
    }

}
