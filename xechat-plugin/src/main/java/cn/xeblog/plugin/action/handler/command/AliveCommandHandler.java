package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.AliveAction;
import cn.xeblog.plugin.action.ConsoleAction;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author anlingyi
 * @date 2021/9/4 8:16 下午
 */
public class AliveCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(String[] args) {
        if (args.length < 1) {
            ConsoleAction.showSimpleMsg("[活着]当前状态：" + (AliveAction.isEnabled() ? "已开启" : "已关闭"));
        } else {
            boolean enabled = Integer.parseInt(args[0]) > 0;
            AliveAction.setEnabled(enabled);
            ConsoleAction.showSimpleMsg("[活着]" + (enabled ? "已开启！" : "已关闭！"));
        }

        if (AliveAction.isEnabled()) {
            boolean flushed = AliveAction.flushNextStartTime();
            ConsoleAction.showSimpleMsg("下一次提醒时间：" + getNextStartTime() + (flushed ? " (已更新)" : ""));
        }
    }

    private static String getNextStartTime() {
        return DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                .format(LocalDateTime.ofInstant(Instant.ofEpochSecond(AliveAction.getNextStartTime()), ZoneId.systemDefault()));
    }

}
