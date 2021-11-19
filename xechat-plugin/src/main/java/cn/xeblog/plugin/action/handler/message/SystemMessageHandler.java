package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.plugin.annotation.DoMessage;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoMessage(MessageType.SYSTEM)
public class SystemMessageHandler extends AbstractMessageHandler<String> {

    @Override
    protected void process(Response<String> response) {
        ConsoleAction.showSystemMsg(response.getTime(), response.getBody());
    }

}
