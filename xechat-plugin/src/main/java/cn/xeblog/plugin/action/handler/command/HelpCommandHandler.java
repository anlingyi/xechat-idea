package cn.xeblog.plugin.action.handler.command;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.entity.TextRender;
import cn.xeblog.plugin.enums.Command;
import cn.xeblog.plugin.enums.Style;
import cn.xeblog.plugin.util.IdeaUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoCommand(Command.HELP)
public class HelpCommandHandler extends AbstractCommandHandler {

    @Override
    public void process(String[] args) {
        List<TextRender> textRenderList = new ArrayList<>();
        for (Command command : Command.values()) {
            textRenderList.add(new TextRender("· " + command.getCommand(), Style.BOLD));
            textRenderList.add(new TextRender("：" + command.getDesc() + "\n", Style.DEFAULT));
        }
        textRenderList.add(new TextRender(" > Tips: ", Style.BOLD));
        textRenderList.add(new TextRender("\"{ }\"表示输入参数占位符，\"[ ]\"内的参数为可选参数，所有参数均以空格分隔。\n", Style.DEFAULT));
        textRenderList.add(new TextRender("\n Version " + IdeaUtils.getPluginVersion() + "\n", Style.BOLD));
        ConsoleAction.atomicExec(() -> {
            ConsoleAction.showSimpleMsg(" 命令列表 & 触发命令前缀 " + Command.COMMAND_PREFIX);
            ConsoleAction.renderText(textRenderList);
            ConsoleAction.renderText(" --------------\n ");
            ConsoleAction.renderUrl("[开源]", "https://github.com/anlingyi/xechat-idea");
            ConsoleAction.renderText("  ");
            ConsoleAction.renderUrl("[更多]", "https://xeblog.cn/?tag=xechat-idea");
            ConsoleAction.renderText("\n --------------\n");
        });
    }

    @Override
    protected boolean check(String[] args) {
        return true;
    }
}
