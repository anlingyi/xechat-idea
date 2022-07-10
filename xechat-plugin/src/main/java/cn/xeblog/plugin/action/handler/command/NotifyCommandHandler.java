package cn.xeblog.plugin.action.handler.command;

import cn.hutool.core.util.NumberUtil;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.enums.Command;

/**
 * @author anlingyi
 * @date 2022/7/1 6:03 下午
 */
@DoCommand(Command.NOTIFY)
public class NotifyCommandHandler extends AbstractCommandHandler {

    @Override
    protected void process(String[] args) {
        String msg = "非法的通知类型！";
        int len = args.length;
        if (len > 0) {
            if (!NumberUtil.isNumber(args[0])) {
                ConsoleAction.showSimpleMsg(msg);
                return;
            }

            int type = Integer.parseInt(args[0]);
            String typeInfo = getNotifyTypeInfo(type);
            if (typeInfo == null) {
                ConsoleAction.showSimpleMsg(msg);
                return;
            }

            DataCache.msgNotify = type;
            ConsoleAction.showSimpleMsg("[" + typeInfo + "]已设置成功！");
        } else {
            ConsoleAction.showSimpleMsg("当前通知类型[" + getNotifyTypeInfo(DataCache.msgNotify) + "]");
        }
    }

    private String getNotifyTypeInfo(int type) {
        switch (type) {
            case 0:
            case 1:
                return "正常通知";
            case 2:
                return "隐晦通知";
            case 3:
                return "关闭通知";
        }

        return null;
    }

    @Override
    protected boolean check(String[] args) {
        return true;
    }
}
