package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.plugin.annotation.DoMessage;
import cn.xeblog.plugin.enums.Style;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoMessage(MessageType.SYSTEM)
public class SystemMessageHandler extends AbstractMessageHandler<String> {

    @Override
    public void handle(Response<String> response) {
        ConsoleAction.renderText(String.format("[%s] 系统消息：%s\n", response.getTime(), response.getBody()),
                Style.SYSTEM_MSG);
    }
}
