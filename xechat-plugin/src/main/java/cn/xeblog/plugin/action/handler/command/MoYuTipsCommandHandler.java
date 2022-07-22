package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.commons.util.MoYuTipsUtil;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.enums.Command;

/**
 * 摸鱼办命令处理程序
 *
 * @author nn200433
 * @date 2022-07-22 07:24:52
 */
@DoCommand(Command.MO_YU)
public class MoYuTipsCommandHandler extends AbstractCommandHandler {

    @Override
    public void process(String[] args) {
        ConsoleAction.showSimpleMsg(MoYuTipsUtil.getTips());
    }

    @Override
    protected boolean check(String[] args) {
        return true;
    }

}
